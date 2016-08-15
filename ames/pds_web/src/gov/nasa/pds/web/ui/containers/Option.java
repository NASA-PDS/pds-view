package gov.nasa.pds.web.ui.containers;

import gov.nasa.pds.web.ui.utils.Comparators;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * A wrapper class for a collection of name value pairs intended to be
 * associated with options of a select box.
 * 
 * @author Joshua Ganderson
 * @version 1.0
 */
// TODO: make an OptionCollection class that extends ArrayList... putting sort,
// select, etc in there. Note that this gets you multiselect behavior and
// _could_ do a toString() on it that prints the markup until that's
// externalized into a custom tag
public class Option implements Serializable, BaseContainerInterface {

    private static final long serialVersionUID = 1L;

    protected Object value;

    // NOTE: sometimes the label must be an int instead of string so that it may
    // be a param in a
    // message format
    // NOTE: defaulting to ""
    protected Object label;

    protected boolean isSelected;

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    // NOTE: since this is intended for markup, null is equivalent to "" and
    // setting that here will assist in sorting
    @SuppressWarnings("nls")
    public Option(Object value, Object label) {
        this.value = value == null ? "" : value;
        this.label = label == null ? "" : label;
    }

    public Option(Object value, Object label, boolean isSelected) {
        this(value, label);
        this.isSelected = isSelected;
    }

    public Object getLabel() {
        return this.label;
    }

    public String getLabelAsString() {
        return this.label.toString();
    }

    public Object getValue() {
        return this.value;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return this.value + " " + this.label + " "
            + ((this.isSelected) ? "selected" : "unselected");
    }

    public int hasCode() {
        return 0;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public static void setSelected(List<Option> optionList, Object value) {
        if (value == null) {
            return;
        }
        for (Option option : optionList) {
            if (option.value.equals(value)) {
                option.setSelected(true);
                break;
            }
        }
    }

    public static void sort(List<Option> optionList) {
        Collections.sort(optionList, Comparators.OPTION_NAME_COMPARATOR);
    }
    
    public static void sortNumeric(List<Option> optionList) {
        Collections.sort(optionList, Comparators.OPTION_NAME_NUMERIC_COMPARATOR);
    }
}
