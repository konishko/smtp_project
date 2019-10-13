package ThreadDispatcher;

import java.util.ArrayList;

public class ThreadMonitor extends Threaded {

    private final ArrayList<Threaded> pull;
    private ArrayList<Threaded> activeThreads;

    public ThreadMonitor(){
        pull = new ArrayList<Threaded>();
        activeThreads = new ArrayList<Threaded>();
    }

    synchronized void update(Threaded th, ThreadStatus status){
        if (status == ThreadStatus.STARTED)
            pull.add(th);
        else
            pull.remove(th);
    }

    public ArrayList<Threaded> getThreads(){
        return activeThreads;
    }

    @Override
    public void doRun(){
        while (true){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
            synchronized (this){
                ArrayList<Threaded> updated = new ArrayList<Threaded>(pull);
                activeThreads = updated;
            }
        }
    }
}
