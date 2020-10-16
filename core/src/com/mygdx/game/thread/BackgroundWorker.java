package com.mygdx.game.thread;

import com.mygdx.game.world.World;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BackgroundWorker {
    private final Thread thread;

    private final Queue<Action> tasks = new ConcurrentLinkedQueue<>();

    public BackgroundWorker(World world){
        thread = new Thread(()->{
            while(world.isRunning()){
                Action action;
                while ((action = tasks.poll()) != null){
                    action.accept();
                }

            }
        });

        thread.start();
    }

    public void schedule(Action action) {
        tasks.add(action);
    }

    public interface Action{
        void accept();
    }
}
