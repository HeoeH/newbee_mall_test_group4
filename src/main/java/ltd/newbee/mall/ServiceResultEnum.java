package ltd.newbee.mall;

public enum ServiceResultEnum {
    SUCCESS("SUCCESS"),
    PARAMETER_ERROR("参数不能为空"),
    DB_ERROR("修改失败");

    private final String result;

    ServiceResultEnum(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
