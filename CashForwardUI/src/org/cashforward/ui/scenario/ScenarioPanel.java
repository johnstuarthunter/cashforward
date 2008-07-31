/*
 * ScenarioPanel.java
 *
 * Created on June 28, 2008, 1:18 PM
 */
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
import org.cashforward.model.Scenario;
import org.cashforward.ui.action.LoadSpecificPaymentsAction;
import org.cashforward.ui.internal.UILogger;
import org.cashforward.ui.task.PaymentFilter;
import org.cashforward.util.DateUtilities;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.StandardXYSeriesLabelGenerator;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.Range;
import org.jfree.experimental.chart.annotations.XYTitleAnnotation;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

/**
 *
 * @author  Bill
 */
public class ScenarioPanel extends javax.swing.JPanel
        implements ChartChangeListener {

    Lookup.Result paymentNotifier =
            UIContext.getDefault().lookupResult(Payment.class);
    Lookup.Result scenarioNotifier =
            UIContext.getDefault().lookupResult(Scenario.class);
    private EventList selectedScenarios = new BasicEventList();
    private JFreeChart chart;
    private TimeSeriesCollection dataset;
    private Map<String, TimeSeries> seriesRegistry = new HashMap();
    private Map<String, Map<String, Float>> scenarioTotals = new HashMap();
    private EventList<Payment> payments;
    boolean isAdjusting;
    Payment previousSelectedPayment;

    /** Creates new form ScenarioPanel */
    public ScenarioPanel() {
        initComponents();

        final JideSplitButton rangeButton =
                new JideSplitButton("Showing: Year to Date");
        rangeButton.setButtonStyle(JideSplitButton.FLAT_STYLE);
        rangeButton.setAlwaysDropdown(true);
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


        refreshChartData();

        dataset = new TimeSeriesCollection();
        chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(Color.WHITE);
        chartContainer.add(chartPanel);

        XYPlot plot = chart.getXYPlot();
        plot.setDomainCrosshairVisible(true);
        plot.setDomainCrosshairLockedOnData(true);
        plot.setDomainCrosshairPaint(Color.BLACK);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        BasicStroke dstroke = new BasicStroke(
                3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                new float[]{3, 1}, 0);
        plot.setDomainCrosshairStroke(dstroke);
        plot.addRangeMarker(new ValueMarker(0));
        // change the auto tick unit selection to integer units only...
        //NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        //rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // add a labelled marker for the safety threshold...
        Marker threshold = new ValueMarker(0.0);
        threshold.setLabelOffsetType(LengthAdjustmentType.EXPAND);
        threshold.setPaint(Color.red);
        threshold.setStroke(new BasicStroke(2.0f));
        //threshold.setLabel("Temperature Threshold");
        //threshold.setLabelFont(new Font("SansSerif", Font.PLAIN, 11));
        //threshold.setLabelPaint(Color.red);
        //threshold.setLabelAnchor(RectangleAnchor.TOP_LEFT);
        //threshold.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
        plot.addRangeMarker(threshold);


        scenarioNotifier.addLookupListener(new LookupListener() {

            public void resultChanged(LookupEvent event) {
                Lookup.Result r = (Lookup.Result) event.getSource();
                
                Collection c = r.allInstances();
                //graph the scenarios
                //will need to actually have the
                //entire set of payments, then 
                //just use the scenario(s) as series
                //which means I need to 
                //just load all payments in at once
                //and then just add series for the matching scenarios
                //OR
                //just make 2 separate loads to the paymentsByScenario
                selectedScenarios.clear();
                if (!c.isEmpty()) {
                    Scenario scenario =
                            (Scenario) c.iterator().next();
                    //selectedScenarios.add(scenario);
                    UILogger.LOG.finest("do something scenario:"+scenario);
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
                null, "Balance",
                dataset,
                PlotOrientation.VERTICAL,
                false, // legend
                true, // tool tips
                false // URLs
                );

        Paint bp = new GradientPaint(0, 0, Color.white, 0, 1000, Color.GRAY);
        chart.setBackgroundPaint(bp);
        chart.setBackgroundImageAlpha(.3f);
        final XYPlot plot = chart.getXYPlot();

        final ValueAxis domainAxis = new DateAxis();
        domainAxis.setTickMarksVisible(true);
        domainAxis.setAutoTickUnitSelection(true);

        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        plot.setDomainAxis(domainAxis);
        plot.setForegroundAlpha(0.5f);

        /*
        final XYAreaRenderer renderer = (XYAreaRenderer) plot.getRenderer();
        renderer.setToolTipGenerator(
                new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new SimpleDateFormat("d-MMM-yyyy"),
                new DecimalFormat("#,##0.00")));
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.yellow,
                0.0f, 0.0f, new Color(0, 0, 64));
        //renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesFillPaint(0, Color.red);
        renderer.setSeriesFillPaint(1, Color.white);
        renderer.setLegendItemToolTipGenerator(
                new StandardXYSeriesLabelGenerator("Tooltip {0}"));
        */
        XYStepRenderer renderer = new XYStepRenderer();
        renderer.setBaseShapesVisible(true);
        renderer.setToolTipGenerator(
                new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new SimpleDateFormat("d-MMM-yyyy"),
                new DecimalFormat("#,##0.00")));
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.yellow,
                0.0f, 0.0f, new Color(0, 0, 64));
        //renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesFillPaint(0, Color.red);
        renderer.setSeriesFillPaint(1, Color.white);
        renderer.setLegendItemToolTipGenerator(
                new StandardXYSeriesLabelGenerator("Tooltip {0}"));
        renderer.setDefaultEntityRadius(6);
        plot.setRenderer(renderer);

        LegendTitle lt = new LegendTitle(plot);
        lt.setItemFont(new Font("Dialog", Font.PLAIN, 9));
        lt.setBackgroundPaint(new Color(200, 200, 255, 80));
        lt.setFrame(new BlockBorder(Color.white));
        lt.setPosition(RectangleEdge.BOTTOM);
        XYTitleAnnotation ta = new XYTitleAnnotation(0.98, 0.90, lt,
                RectangleAnchor.BOTTOM_RIGHT);

        ta.setMaxWidth(0.48);
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
                if (!selectedScenarios.contains(scenario))
                    continue;
                UILogger.LOG.finest("---"+scenario.getName()+"---");
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

        //update min/max, but this is date, not value!!!
        Range bounds = dataset.getDomainBounds(false);
        if (bounds == null)
            return;
        
        Marker lowPoint = new ValueMarker(bounds.getLowerBound(), Color.red,
                new BasicStroke(2.0f));
        lowPoint.setPaint(Color.red);
        lowPoint.setStroke(new BasicStroke(2.0f));
        lowPoint.setLabel("Low point: ($"+ bounds.getLowerBound()+")");
        lowPoint.setLabelFont(new Font("SansSerif", Font.PLAIN, 11));
        lowPoint.setLabelPaint(Color.red);
        lowPoint.setLabelAnchor(RectangleAnchor.BOTTOM_LEFT);
        lowPoint.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);

        Marker highPoint = new ValueMarker(bounds.getUpperBound(), Color.black,
                new BasicStroke(2.0f));
        highPoint.setPaint(Color.gray);
        highPoint.setStroke(new BasicStroke(2.0f));
        highPoint.setLabel("High point: $"+ bounds.getUpperBound());
        highPoint.setLabelFont(new Font("SansSerif", Font.PLAIN, 11));
        highPoint.setLabelPaint(Color.black);
        highPoint.setLabelAnchor(RectangleAnchor.BOTTOM_LEFT);
        highPoint.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
        UILogger.LOG.finest("Bounds:"+bounds);
        XYPlot plot = chart.getXYPlot();
        plot.addDomainMarker(lowPoint, Layer.BACKGROUND);
        plot.addDomainMarker(highPoint, Layer.BACKGROUND);
    }

    private void updateValue(String name, Day day, float value) {
        TimeSeries s = getSeries(name);
        s.update(day, value);
        seriesRegistry.put(name, s);
    }

    private void insertValue(String name, Day day, float value) {
        TimeSeries s = getSeries(name);
        s.add(day,value);
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
