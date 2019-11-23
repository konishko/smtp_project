package ThreadDispatcher;

public class ThreadDispatcher {
    private static volatile ThreadDispatcher instance;
    public ThreadMonitor monitor;
    private ThreadDispatcher(){
        monitor = new ThreadMonitor();
        Add(monitor);
    }

    public static ThreadDispatcher getInstance(){
        ThreadDispatcher localInstance = instance;
        if(localInstance == null)
            synchronized (ThreadDispatcher.class)
            {
                localInstance = instance;
                if(localInstance == null)
                    instance = localInstance = new ThreadDispatcher();
            }
        return localInstance;
    }

    public void Add(Threaded th){
        th.setDispatcher(this);
        Thread t = new Thread(th);
        th.id = th.getClass().getName() + " " + t.getId();
        monitor.update(th, ThreadStatus.STARTED);
        t.start();
    }

}
