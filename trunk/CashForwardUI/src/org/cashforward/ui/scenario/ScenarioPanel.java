package org.cashforward.ui.scenario;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import com.jidesoft.popup.JidePopup;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.cashforward.model.Payment;
import org.cashforward.model.PaymentSearchCriteria;
import org.cashforward.ui.UIContext;
import org.cashforward.ui.adapter.PaymentServiceAdapter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import com.jidesoft.swing.JideSplitButton;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.UIManager;
import org.cashforward.model.Scenario;
import org.cashforward.ui.action.LoadSpecificPaymentsAction;
import org.cashforward.ui.internal.UILogger;
import org.cashforward.ui.task.PaymentFilter;
import org.cashforward.util.DateUtilities;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.PeriodAxis;
import org.jfree.chart.axis.PeriodAxisLabelInfo;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.StandardXYSeriesLabelGenerator;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.Month;
import org.jfree.data.time.Year;
import org.jfree.experimental.chart.annotations.XYTitleAnnotation;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

/**
 * Plots the cashflow for the selected scenario. If more than one 
 * Scenario is selected, each Scenario's cashflow is shown.
 *
 * //TODO extract Strings to NbBundle
 * 
 * @author Bill
 */
public class ScenarioPanel extends javax.swing.JPanel
        implements ChartChangeListener {

    Lookup.Result paymentNotifier =
            UIContext.getDefault().lookupResult(Payment.class);
    Lookup.Result scenarioNotifier =
            UIContext.getDefault().lookupResult(Scenario.class);
    private EventList selectedScenarios = new BasicEventList();
    private JideSplitButton rangeButton;
    private JFreeChart chart;
    private ChartPanel chartPanel;
    private TimeSeriesCollection dataset;
    private Map<String, TimeSeries> seriesRegistry = new HashMap();
    private Map<String, Map<String, Float>> scenarioTotals = new HashMap();
    private EventList<Payment> payments;
    boolean isAdjusting;
    Payment previousSelectedPayment;

    /** Creates new form ScenarioPanel */
    public ScenarioPanel() {
        initComponents();

        rangeButton =
                new JideSplitButton("Showing: Through this Year");
        rangeButton.setButtonStyle(JideSplitButton.FLAT_STYLE);
        rangeButton.setAlwaysDropdown(true);
        rangeButton.add(new AbstractAction("Through this Year") {

            public void actionPerformed(ActionEvent e) {
                PaymentFilter filter = new PaymentFilter();
                Date today = new Date();
                filter.setDateStart(today);
                filter.setDateEnd(DateUtilities.endOfThisYear());
                filter.setScenarios(UIContext.getDefault().getSelectedScenarios());
                UIContext.getDefault().setPaymentFilter(filter);
                new LoadSpecificPaymentsAction().actionPerformed(null);
                rangeButton.setText((String) this.getValue(Action.NAME));
            }
        });
        rangeButton.add(new AbstractAction("Next Three Months") {

            public void actionPerformed(ActionEvent e) {
                PaymentFilter filter = new PaymentFilter();
                Date today = new Date();
                filter.setDateStart(today);
                filter.setDateEnd(
                        DateUtilities.getDateAfterPeriod(today, Calendar.MONTH, 3));
                filter.setScenarios(UIContext.getDefault().getSelectedScenarios());
                UIContext.getDefault().setPaymentFilter(filter);
                new LoadSpecificPaymentsAction().actionPerformed(null);
                rangeButton.setText((String) this.getValue(Action.NAME));
            }
        });
        rangeButton.add(new AbstractAction("Next Six Months") {

            public void actionPerformed(ActionEvent e) {
                PaymentFilter filter = new PaymentFilter();
                Date today = new Date();
                filter.setDateStart(today);
                filter.setDateEnd(
                        DateUtilities.getDateAfterPeriod(today, Calendar.MONTH, 6));
                UIContext.getDefault().setPaymentFilter(filter);
                new LoadSpecificPaymentsAction().actionPerformed(null);
                rangeButton.setText((String) this.getValue(Action.NAME));
            }
        });
        final JidePopup popup = new JidePopup();
        popup.setMovable(true);
        popup.getContentPane().setLayout(new BorderLayout());
        popup.getContentPane().add(customDatePanel);
        popup.setDefaultFocusComponent(startDateCombo);
        rangeButton.add(new AbstractAction("Choose dates") {

            public void actionPerformed(ActionEvent e) {
                popup.updateUI();
                popup.setOwner(rangeButton);
                popup.setResizable(true);
                popup.setMovable(true);
                if (popup.isPopupVisible()) {
                    popup.hidePopup();
                } else {
                    popup.showPopup();
                }
            }
        });
        rangeButtonContainer.add(rangeButton);

        //do the inital chart plotting
        refreshChartData();

        dataset = new TimeSeriesCollection();
        chart = createChart(dataset);
        chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(Color.WHITE);
        chartContainer.add(BorderLayout.CENTER, chartPanel);

        XYPlot plot = chart.getXYPlot();
        plot.setDomainCrosshairVisible(true);
        plot.setDomainCrosshairLockedOnData(true);
        plot.setDomainCrosshairPaint(Color.BLACK);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        BasicStroke dstroke = new BasicStroke(2);
        plot.setDomainCrosshairStroke(dstroke);
        plot.addRangeMarker(new ValueMarker(0));
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        

        //--AXIS prototyping
        // change the auto tick unit selection to integer units only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        Font rfont = rangeAxis.getLabelFont();
        rangeAxis.setLabelFont(rfont.deriveFont(Font.BOLD, rfont.getSize() + 2));
        //rangeAxis.setLabelPaint(Color.WHITE);

        PeriodAxis domainAxis = new PeriodAxis("");
        domainAxis.setAutoRangeTimePeriodClass(Day.class);
        //Font dfont = domainAxis.getLabelFont();
        //domainAxis.setLabelFont(dfont.deriveFont(Font.BOLD, dfont.getSize() + 2));
        domainAxis.setAutoRangeTimePeriodClass(Day.class);
        PeriodAxisLabelInfo[] info = new PeriodAxisLabelInfo[3];
        info[0] = new PeriodAxisLabelInfo(Day.class, new SimpleDateFormat("d"));
        //info[1] = new PeriodAxisLabelInfo(Day.class, new SimpleDateFormat("E"),
        //    new RectangleInsets(2, 2, 2, 2), new Font("SansSerif", Font.BOLD,
        //    10), Color.blue, false, new BasicStroke(0.0f), Color.lightGray);
        info[1] = new PeriodAxisLabelInfo(Month.class,
                new SimpleDateFormat("MMM"));
        info[2] = new PeriodAxisLabelInfo(Year.class,
                new SimpleDateFormat("yyyy"));
        domainAxis.setLabelInfo(info);
        plot.setDomainAxis(domainAxis);


        // add a labelled marker for the zero threshold...
        Marker threshold = new ValueMarker(0.0);
        threshold.setLabelOffsetType(LengthAdjustmentType.EXPAND);
        threshold.setPaint(Color.BLACK);
        threshold.setStroke(new BasicStroke(1.0f));
        threshold.setLabel("$0.00");
        threshold.setLabelFont(new Font("SansSerif", Font.PLAIN, 11));
        threshold.setLabelPaint(Color.gray);
        threshold.setLabelAnchor(RectangleAnchor.TOP_LEFT);
        threshold.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
        plot.addRangeMarker(threshold);


        scenarioNotifier.addLookupListener(new LookupListener() {

            public void resultChanged(LookupEvent event) {
                Lookup.Result r = (Lookup.Result) event.getSource();

                Collection c = r.allInstances();
                //graph the scenarios
                //will need to actually have the
                //entire set of payments, then 
                //just use the scenario(s) as series
                selectedScenarios.clear();
                if (!c.isEmpty()) {
                    Scenario scenario =
                            (Scenario) c.iterator().next();
                    UILogger.LOG.finest("do something scenario:" + scenario);
                    selectedScenarios.addAll(UIContext.getDefault().getSelectedScenarios());
                    refreshChartData();
                }
            }
        });

        paymentNotifier.addLookupListener(new LookupListener() {

            public void resultChanged(LookupEvent event) {
                Lookup.Result r = (Lookup.Result) event.getSource();
                Collection c = r.allInstances();
                if (!c.isEmpty()) {
                    Payment payment = (Payment) c.iterator().next();
                    isAdjusting = true;
                    chart.getXYPlot().setDomainCrosshairValue(
                            payment.getStartDate().getTime());
                    isAdjusting = false;
                }
            }
        });

        chart.addChangeListener(this);
    }

    public void setPaymentFilter(PaymentFilter paymentFilter) {
        startDateCombo.setDate(paymentFilter.getDateStart());
        endDateCombo.setDate(paymentFilter.getDateEnd());
    }

    public void setPayments(EventList payments) {
        this.payments = payments;

        refreshChartData();
        payments.addListEventListener(new ListEventListener() {

            public void listChanged(ListEvent event) {
                if (!event.isReordering()) {
                    refreshChartData();
                    if (event.getSourceList().size() == 0) {
                        chartPanel.setVisible(false);
                        rangeButtonContainer.setVisible(false);
                    } else {
                        chartPanel.setVisible(true);
                        rangeButtonContainer.setVisible(true);
                    }
                }
            }
        });
    }

    public void chartChanged(ChartChangeEvent event) {
        if (isAdjusting) {
            //isAdjusting = false;
            UILogger.LOG.finest("skipping chartChanged");
            return;
        }
        XYPlot plot = (XYPlot) chart.getPlot();
        double time = plot.getDomainCrosshairValue();
        setSelectedDate(new Date((long) time));
    }

    private void setSelectedDate(Date date) {
        for (Payment payment : payments) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            Calendar scal = Calendar.getInstance();
            scal.setTime(payment.getStartDate());

            boolean sameDate = cal.get(Calendar.YEAR) == scal.get(Calendar.YEAR);
            sameDate &= cal.get(Calendar.DAY_OF_MONTH) == scal.get(Calendar.DAY_OF_MONTH);
            sameDate &= cal.get(Calendar.MONTH) == scal.get(Calendar.MONTH);
            //just of going up, we need to set the last one not the first one
            if (sameDate) {
                if (payment.equals(previousSelectedPayment)) {
                    UILogger.LOG.finest("same");
                    continue;
                }
                isAdjusting = true;
                UILogger.LOG.finest("settingp:" + payment);
                UIContext.getDefault().setPayment(payment);
                previousSelectedPayment = payment;
                return;
            }
            previousSelectedPayment = payment;
        }
    }

    private JFreeChart createChart(final XYDataset dataset) {
        chart = //ChartFactory.createXYAreaChart(
                ChartFactory.createXYLineChart(
                null,
                null, null,
                dataset,
                PlotOrientation.VERTICAL,
                false, // legend
                true, // tool tips
                false // URLs
                );

        
        Paint bp = new GradientPaint(
                0f, 0f, UIManager.getColor("Panel.background"),
                0f, 100f, Color.WHITE);
        chart.setBackgroundPaint(bp);
        chart.setBackgroundImageAlpha(.3f);
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(null);

        final ValueAxis domainAxis = new DateAxis();
        domainAxis.setTickMarksVisible(true);
        domainAxis.setAutoTickUnitSelection(true);

        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        plot.setDomainAxis(domainAxis);
        plot.setForegroundAlpha(0.5f);

        XYStepRenderer renderer = new XYStepRenderer();
        renderer.setToolTipGenerator(
                new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new SimpleDateFormat("d-MMM-yyyy"),
                new DecimalFormat("#,##0.00")));
        //GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.yellow,
        //        0.0f, 0.0f, new Color(0, 0, 64));
        //renderer.setSeriesPaint(0, gp0);
        renderer.setLegendItemToolTipGenerator(
                new StandardXYSeriesLabelGenerator("Tooltip {0}"));
        renderer.setDefaultEntityRadius(6);
        plot.setRenderer(renderer);

        LegendTitle lt = new LegendTitle(plot);
        lt.setItemFont(new Font("Dialog", Font.PLAIN, 10));
        lt.setBackgroundPaint(new Color(200, 200, 255, 95));
        lt.setFrame(new BlockBorder(Color.white));
        lt.setPosition(RectangleEdge.BOTTOM);
        XYTitleAnnotation ta = new XYTitleAnnotation(0.98, 0.90, lt,
                RectangleAnchor.BOTTOM_RIGHT);

        ta.setMaxWidth(0.68);
        plot.addAnnotation(ta);
        return chart;
    }

    private void refreshChartData() {

        if (payments == null) {
            return;
        }
        //clear everything
        dataset.removeAllSeries();
        seriesRegistry.clear();
        scenarioTotals.clear();

        //Map<String, Float> totals = new HashMap();
        float value = 0.0f;
        Day day = null;
        for (Payment payment : payments) {
            day = new Day(payment.getStartDate());

            UILogger.LOG.finest("day:" + day);
            //for each scenario, calculate the total
            List<Scenario> scenarios = payment.getScenarios();
            Map<String, Float> totals;
            for (Scenario scenario : scenarios) {
                if (!selectedScenarios.contains(scenario)) {
                    continue;
                }
                UILogger.LOG.finest("---" + scenario.getName() + "---");
                totals = getTotals(scenario.getName());

                if (totals.containsKey(day.toString())) {
                    value += totals.get(day.toString()) + payment.getAmount();
                    UILogger.LOG.finest("new total for " + day + " is " + value);
                    totals.put(day.toString(), value);
                    updateValue(scenario.getName(), day, value);
                } else {
                    value += payment.getAmount();
                    UILogger.LOG.finest("total for " + day + " is " + value);
                    totals.put(day.toString(), value);
                    insertValue(scenario.getName(), day, value);
                }

                scenarioTotals.put(scenario.getName(), totals);
            }
        }

        Collection<TimeSeries> series = seriesRegistry.values();
        for (TimeSeries timeSeries : series) {
            dataset.addSeries(timeSeries);
        }
    }

    private void updateValue(String name, Day day, float value) {
        TimeSeries s = getSeries(name);
        s.update(day, value);
        seriesRegistry.put(name, s);
    }

    private void insertValue(String name, Day day, float value) {
        TimeSeries s = getSeries(name);
        s.add(day, value);
        seriesRegistry.put(name, s);
    }

    private TimeSeries getSeries(String name) {
        if (seriesRegistry.containsKey(name)) {
            return seriesRegistry.get(name);
        } else {
            TimeSeries s = new TimeSeries(name);
            seriesRegistry.put(name, s);
            return s;
        }
    }

    private Map<String, Float> getTotals(String name) {
        if (scenarioTotals.containsKey(name)) {
            return scenarioTotals.get(name);
        } else {
            Map m = new HashMap();
            scenarioTotals.put(name, m);
            return m;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        customDatePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        startDateCombo = new com.jidesoft.combobox.DateComboBox();
        jLabel2 = new javax.swing.JLabel();
        endDateCombo = new com.jidesoft.combobox.DateComboBox();
        jButton1 = new javax.swing.JButton();
        chartContainer = new javax.swing.JPanel();
        rangeButtonContainer = new javax.swing.JPanel();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(ScenarioPanel.class, "ScenarioPanel.jLabel1.text")); // NOI18N

        startDateCombo.setPreferredSize(new java.awt.Dimension(92, 20));

        jLabel2.setText(org.openide.util.NbBundle.getMessage(ScenarioPanel.class, "ScenarioPanel.jLabel2.text")); // NOI18N

        endDateCombo.setPreferredSize(new java.awt.Dimension(92, 20));

        jButton1.setText(org.openide.util.NbBundle.getMessage(ScenarioPanel.class, "ScenarioPanel.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout customDatePanelLayout = new org.jdesktop.layout.GroupLayout(customDatePanel);
        customDatePanel.setLayout(customDatePanelLayout);
        customDatePanelLayout.setHorizontalGroup(
            customDatePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(customDatePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(startDateCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(endDateCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButton1)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        customDatePanelLayout.setVerticalGroup(
            customDatePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(customDatePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(customDatePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(startDateCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(endDateCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton1))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        chartContainer.setLayout(new java.awt.BorderLayout());

        rangeButtonContainer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(chartContainer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .add(rangeButtonContainer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(rangeButtonContainer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(chartContainer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    PaymentServiceAdapter paymentService =
            new PaymentServiceAdapter();
    PaymentSearchCriteria search = new PaymentSearchCriteria();
    search.setDateStart(startDateCombo.getDate());
    search.setDateEnd(endDateCombo.getDate());

    UIContext.getDefault().setPaymentFilter(new PaymentFilter(search));
    new LoadSpecificPaymentsAction().actionPerformed(null);
/*
List newPayments = paymentService.getPayments(paymentFilter);
if (newPayments == null) {
return;
}
payments.getReadWriteLock().writeLock().lock();
payments.clear();
payments.addAll(newPayments);
payments.getReadWriteLock().writeLock().unlock();

refreshChartData();
 */
}//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chartContainer;
    private javax.swing.JPanel customDatePanel;
    private com.jidesoft.combobox.DateComboBox endDateCombo;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel rangeButtonContainer;
    private com.jidesoft.combobox.DateComboBox startDateCombo;
    // End of variables declaration//GEN-END:variables
}
