import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Element {
    private Lock lock = new ReentrantLock();
    private Integer info;

    public Element(Integer info) {
        this.info = info;
    }

    public Element(){
        this.info = null;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public Integer getInfo() {
        return info;
    }

    public void setInfo(Integer info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return info.toString();
    }
}
