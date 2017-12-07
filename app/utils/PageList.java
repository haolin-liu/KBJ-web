package utils;

import java.util.List;

/**
 * Wrap the data list in current page into a Class with pagination info.
 * @param <T>
 * @author jie-z
 * @date 2017/12/07
 */
public class PageList<T> {

    private List<T> list;

    private long recordCount;
    private int pageIndex;
    private int pageSize;

    private int pageCount;
    private final static int navigationCount = 10;
    private int navigationFrom;
    private int navigationTo;
    private int navigationMiddle;

    /**
     * Wrap the data list in current page into a Class with pagination info.
     * @param recordCount the total record count.
     * @param pageIndex 1 based page index of the total page count.
     * @param pageSize max rows in one page.
     * @param list the list of entities for this page.
     * @throws Exception
     */
    public PageList(long recordCount, int pageIndex, int pageSize, List<T> list) throws Exception {
        this.list = list;
        setRecordCount(recordCount);
        setPageIndex(pageIndex);
        setPageSize(pageSize);

        setPageCount();
        setNavigationMiddle();
        setNavigationFrom();
        setNavigationTo();
    }

    /**
     * Return the list of entities for this page.
     * @return
     */
    public List<T> getList() {
        return list;
    }

    /**
     * Return the total row count for all pages.
     * @return
     */
    public long getRecordCount() {
        return recordCount;
    }

    /**
     * Return the index position of this page. (1 based).
     * @return
     */
    public int getPageIndex() {
        return pageIndex;
    }

    /**
     * Return the record size in one page.
     * @return
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Return the total page count.
     * This is a calculated value based on recordCount/pageSize.
     * @return
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * Return the from page index of a pagination navigation bar.
     * @return
     */
    public int getNavigationFrom() {
        return navigationFrom;
    }

    /**
     * Return the to page index of a pagination navigation bar.
     * @return
     */
    public int getNavigationTo() {
        return navigationTo;
    }

    /**
     * Return whether if there is a previous page of this page.
     * @return
     */
    public boolean hasPrevPage() {
        return this.pageIndex > 1;
    }

    /**
     * Return whether if there is a next page of this page.
     * @return
     */
    public boolean hasNextPage() {
        return this.pageIndex < this.pageCount;
    }


    private void setRecordCount(long recordCount) throws Exception {
        if (recordCount >= 0) {
            this.recordCount = recordCount;
        } else {
            throw new Exception("record count should be an integer equals or above 0.");
        }
    }

    private void setPageIndex(int pageIndex) throws Exception {
        if (pageIndex >= 1) {
            this.pageIndex = pageIndex;
        } else {
            throw new Exception("page index is a 1 based integer.");
        }
    }

    private void setPageSize(int pageSize) throws Exception {
        if (pageSize > 0) {
            this.pageSize = pageSize;
        } else {
            throw new Exception("page size should be an integer above 0.");
        }
    }

    /**
     * Calc the index position of this page (1 based).
     * This is a calculated value based on recordCount/pageSize.
     */
    private void setPageCount() {
        this.pageCount = (int)Math.ceil(this.recordCount / (double)this.pageSize);
    }

    /**
     * Calc the middle index of the pagination navigation bar.
     *   for the navigation bar with 10 pages, the value is 6.
     *   just next to the middle value of 10.
     */
    private void setNavigationMiddle() {
        int middle = (int)Math.ceil(this.navigationCount / 2.0) + 1;
        this.navigationMiddle = middle < this.navigationCount ? middle : this.navigationCount;
    }

    /**
     * Calc the from page index of the pagination navigation bar.
     *   for the navigation bar with 10 pages, the current page index is 7,
     *   the from page index will be 2 and the end index will be 11.
     */
    private void setNavigationFrom() {
        if (this.pageCount > this.navigationCount
                && this.pageIndex > this.navigationMiddle) {
            this.navigationFrom = this.pageIndex - this.navigationMiddle + 1;
        } else {
            this.navigationFrom = 1;
        }
    }

    private void setNavigationTo() {
        int to = this.navigationFrom + this.navigationCount - 1;
        this.navigationTo = to < this.pageCount ? to : this.pageCount;
    }

}
