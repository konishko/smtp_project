package ThreadDispatcher;

public abstract class Threaded implements Runnable{
    protected ThreadDispatcher dispatcher;
    public String id;
    public ThreadDispatcher getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(ThreadDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    private void notifyMonitor(){
        if(dispatcher != null && dispatcher.monitor != null)
            dispatcher.monitor.update(this, ThreadStatus.FINISHED);
    }

    public abstract void doRun();

    @Override
    public void run(){
        try{
            doRun();
        }
        finally {
            notifyMonitor();
        }
    }
}
