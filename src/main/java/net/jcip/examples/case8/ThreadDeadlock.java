package net.jcip.examples.case8;

import java.util.concurrent.*;

/**
 * ThreadDeadlock
 * <p/>
 * Task that deadlocks in a single-threaded Executor
 *
 * @author Brian Goetz and Tim Peierls
 */
public class ThreadDeadlock {
    ExecutorService exec = Executors.newSingleThreadExecutor();

    RenderPageTask renderPageTask = new RenderPageTask();

    public RenderPageTask getRenderPageTask() {
        return renderPageTask;
    }

    public void setRenderPageTask(RenderPageTask renderPageTask) {
        this.renderPageTask = renderPageTask;
    }

    public ThreadDeadlock() throws Exception {
        //String call = new RenderPageTask().call();
    }

    public class LoadFileTask implements Callable<String> {
        private final String fileName;

        public LoadFileTask(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public String call() throws Exception {
            // Here's where we would actually read the file
            return "";
        }
    }

    public class RenderPageTask implements Callable<String> {
        @Override
        public String call() throws Exception {
            Future<String> header, footer;
            header = exec.submit(new LoadFileTask("header.html"));
            footer = exec.submit(new LoadFileTask("footer.html"));
            String page = renderBody();
            // Will deadlock -- task waiting for result of subtask
            return header.get() + page + footer.get();
        }

        private String renderBody() {
            // Here's where we would actually render the page
            return "";
        }
    }

    public static void main(String[] args) throws Exception {
        ThreadDeadlock threadDeadlock = new ThreadDeadlock();
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<String> submit = executor.submit(threadDeadlock.getRenderPageTask());
        String s = submit.get();
        System.out.println("开始输出："+s);
    }
}
