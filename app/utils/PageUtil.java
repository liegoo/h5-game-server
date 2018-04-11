package utils;

import constants.GlobalConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by roy@warthog.cn on 2014/9/25.
 */
public class PageUtil {

    public static PageInfo getPageInfo(int page, int pageSize) {
        if (page <= 0) {
            page = GlobalConstants.DEFAULT_PAGE;
        }

        if (pageSize <= 0 && pageSize != -1) {
            pageSize = GlobalConstants.DEFAULT_PAGE_SIZE;
        }
        return new PageInfo(page, pageSize);
    }

    public static final class PageInfo {

        public PageInfo(int page, int pageSize) {
            this.page = page;
            this.pageSize = pageSize;
        }

        private int page;

        private int pageSize;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
    }

    /**
     * 对List进行软分页
     * @param tList
     * @param page
     * @param pageSize
     * @param <T>
     * @return
     */
    public static <T> List<T> getByPage(List<T> tList, int page, int pageSize) {
        if (pageSize == -1) {
            return tList;
        }

        if ((page - 1) * pageSize >= tList.size()) {
            return new ArrayList<T>();
        } else if (page * pageSize > tList.size()) {
            return tList.subList((page - 1) * pageSize, tList.size());
        } else {
            return tList.subList((page - 1) * pageSize, page * pageSize);
        }
    }

    /**
     * 打乱list并随机获取指定数量的元素
     * @param tList
     * @param size
     * @param <T>
     * @return
     */
    public static <T> List<T> getRandomSize(List<T> tList, int size) {
        Collections.shuffle(tList);

        if (tList.size() < size) {
            return tList;
        }
        return tList.subList(0, size);
    }

}
