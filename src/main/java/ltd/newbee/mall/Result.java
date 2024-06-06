package ltd.newbee.mall;

public class Result<T> {
    private String result;
    private boolean success;

    public Result(String result, boolean success) {
        this.result = result;
        this.success = success;
    }

    public String getResult() {
        return result;
    }

    public boolean isSuccess() {
        return success;
    }
}
