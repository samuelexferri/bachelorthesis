package com.unibg.app3dsat.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: Group
 */
public class Group {
    public final List<String> children = new ArrayList<>();
    public final String string;

    /**
     * Constructor: Group
     *
     * @param string
     */
    public Group(String string) {
        this.string = string;
    }
}