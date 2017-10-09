package com.example.vi_tu.gtinteractive.utilities;

import java.util.List;

/**
 * Created by Rayner on 10/8/17.
 */

public class BaseFilter<T> {
    List<T> list;

    public BaseFilter(List<T> list) { setList(list); }

    public void setList(List<T> list) { this.list = list; }

    public List<T> getList() { return list; }
}
