# Introduction #

The NetBeans Lookup mechanism provides support for managing selection, or application context. For instance, if I want to know when a Payment has been selected, I can ask the Lookup context to tell me. This allows for a cleaner separation of the interface and other application components, and reduces the amount of code I need to write.

NetBeans has two ways of handling context: a Lookup within the application Windows (`TopComponents`) and within the application `Nodes` (`Nodes` are a [hierarchical presentation model](http://wiki.netbeans.info/wiki/view/DevFaqWhatIsANode)).

(See the [NetBeans Selection Management Tutorial](http://platform.netbeans.org/tutorials/60/nbm-selection-1.html) and the [Lookup Library docs](http://openide.netbeans.org/lookup/) for more detailed information on how the Lookup works.)

# A Central Lookup #

CashForward [doesn't use Nodes](CashForwardDesign.md) at the moment, and so utilizes the `TopComponent` approach to handling context. Each `TopComponent` has its own collection of objects and state, which it holds in a map. Other objects are able to put things in the collection and ask for things back from each `TopComponent`.

The `UIContext` takes this a step further and provides one central Lookup implementation for TopComponents and other parts of the interface to share. This Lookup is initialized apart from the rest of the interface. It also uses the [GlazedLists](http://publicobject.com/glazedlists/) `EventList` pipeline so to allow components to observe changes to master lists.

# A Closer Look #
Because its a standalone context, `UIContext` extends `AbstractLookup`. All the objects added to the `UIContext` are put into a copy of `InstanceContent`. `InstanceContent` provides the storing, querying, and notification capabilities for the Lookup.

So we have

```

public class UIContext extends AbstractLookup { 
    private InstanceContent content = null;
    private static UIContext ctx = new UIContext();
    
    private UIContext() {
        this(new InstanceContent());
    }

    private UIContext(InstanceContent content) {
        super(content);
        this.content = content;
    }
}

```

except that there are two methods to implement for `AbstractLookup`. The ones to add and remove objects from the Lookup:

```
    public void add(Object instance) {
        content.add(instance);
    }

    public void remove(Object instance) {
        content.remove(instance);
    }
```

Now all we need is a convenience method for objects we may set often:

```
    /**
     * Set the <code>Payment</code> being used by the application
     *
     * @param payment the new <code>Payment</code> to use.
     */
    public synchronized void setPayment(Payment payment) {
        
        add(payment);
    }
```

And that is it. Almost. `InstanceContent` allows multiple objects of the same class in the Lookup. So the above method needs adjusted; the lookupAll method is used to read all the instances of `Payment` in the `UIContext`.

```
     /**
     * Set the <code>Payment</code> being used by the application
     *
     * @param payment the new <code>Payment</code> to use.
     */
    public synchronized void setPayment(Payment payment) {
        Collection all =
                lookupAll(Payment.class);
        if (all != null) {
            Iterator ia = all.iterator();
            while (ia.hasNext()) {
                remove(ia.next());
            }
        }
        add(payment);
    }
```


Once the `Payment` is in the context, someone will need to know about it. The first thing is to ask the Lookup for the object via the lookupResult method.

```
     Lookup.Result paymentNotifier =
            UIContext.getDefault().lookupResult(Payment.class);
```

`Lookup.Result` will give you all the available instances of the `Payment` in the context. However, we are interested in listening for the result to change. If we attach a `LookupListener` to the `Lookup.Result` we will receive notifications when a `Payment` is added or removed:

```
    paymentNotifier.addLookupListener(new LookupListener() {

            public void resultChanged(LookupEvent event) {
                Lookup.Result r = (Lookup.Result) event.getSource();
                Collection c = r.allInstances();
                if (!c.isEmpty()) {
                    payment = (Payment) c.iterator().next();
            }
        });

```


