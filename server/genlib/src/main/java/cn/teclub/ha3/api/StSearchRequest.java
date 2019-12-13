package cn.teclub.ha3.api;


/**
 * @author Tao Zhang
 */
public class StSearchRequest extends StRequestBody {

    private  String keyword;

    private  Integer page;

    private  Integer size;

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPage() {
        return page;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getSize() {
        return size;
    }
}
