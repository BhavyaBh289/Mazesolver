package mazesolver;

class BackgroundWorker implements IConnectWorker, IConnectUI{

    Grid grid;
    int algorithm;
    boolean started;
    boolean stopAlgorithm;
    boolean workerStopped;
    boolean finished;

    public synchronized boolean workerStopped() {
        return workerStopped;
    }

    public synchronized void stopWorker(boolean stopWorker) {
        this.workerStopped = stopWorker;
        if (stopWorker) {
            stopAlgorithm = true;
        }

        notifyAll();
    }

    public synchronized void start() throws InterruptedException {

        while (!(started || workerStopped)) {
            this.wait();
        }

        started = false;
        finished = false;
        stopAlgorithm = false;
    }

    public synchronized boolean isStarted() {
        return started;
    }

    public synchronized void finished() {
        finished = true;
    }

    public synchronized boolean isFinished() {
        return finished;
    }

    public synchronized void setStarted(boolean started) {
        this.started = started;

        notifyAll();
    }

    public synchronized boolean algorithmStopped() {
        return stopAlgorithm;
    }

    public void stopAlgorithm(boolean stopAlgorithm) {
        this.stopAlgorithm = stopAlgorithm;
    }

    public synchronized int getAlgorithm() {
        return algorithm;
    }

    public synchronized void setAlgorithm(int algorithm) {
        this.algorithm = algorithm;
    }

    public synchronized void setGrid(Grid grid) {
        this.grid = grid;
    }

    public synchronized Grid getGrid() {
        return grid;
    }

    public void stopRunning() {
        GUI.running = false;
        stopAlgorithm(true);
    }
}
class Worker extends Thread {

    IConnectWorker worker;

    public Worker(IConnectWorker worker) {
        this.worker = worker;
    }

    @Override
    public void run() {
        try {
            do {
                worker.start();
                if (worker.workerStopped()) {
                    break;
                }
                Grid grid = worker.getGrid();
                RunAlgorithm algorithm = new RunAlgorithm();
                algorithm.setWorker(worker);
                algorithm.setGrid(grid);
                algorithm.start();
                worker.finished();
            } while (!worker.workerStopped());
        } catch (InterruptedException ex) {
        }
    }

}
interface IConnectWorker {

    void finished();

    int getAlgorithm();

    Grid getGrid();

    boolean algorithmStopped();

    boolean workerStopped();

    void start() throws InterruptedException;

    void stopRunning();
}
interface IConnectUI {

    boolean isStarted();

    void setAlgorithm(int algo);

    void setGrid(Grid grid);

    void stopAlgorithm(boolean shouldStop);

    void setStarted(boolean start);

    void stopWorker(boolean stopWorker);

    boolean isFinished();
}
class RunAlgorithm {

    Grid grid;
    IConnectWorker worker;

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public void setWorker(IConnectWorker worker) {
        this.worker = worker;
    }

    public void start() throws InterruptedException {
        AlgorithmFactory.getAlgorithm().solve(worker, grid);
    }
}
