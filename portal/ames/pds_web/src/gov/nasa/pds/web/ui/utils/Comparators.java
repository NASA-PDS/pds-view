package gov.nasa.pds.web.ui.utils;

import gov.nasa.arc.pds.tools.util.StrUtils;
import gov.nasa.pds.web.ui.actions.BaseAction;
import gov.nasa.pds.web.ui.actions.dataManagement.ShowResults.DataSetProblem;
import gov.nasa.pds.web.ui.actions.dataManagement.ShowResults.ProblemCluster;
import gov.nasa.pds.web.ui.actions.dataManagement.ShowResults.ProblemGroup;
import gov.nasa.pds.web.ui.actions.dataManagement.ShowResults.SetNode;
import gov.nasa.pds.web.ui.containers.ExceptionContainer;
import gov.nasa.pds.web.ui.containers.Option;
import gov.nasa.pds.web.ui.containers.VolumeContainer;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults.NewValue;
import gov.nasa.pds.web.ui.containers.tabularData.Column;
import gov.nasa.pds.web.ui.containers.tabularData.Element;
import gov.nasa.pds.web.ui.containers.tabularManagement.ColumnCheckbox;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceColumn;

import java.util.Comparator;

public class Comparators {

	public static final Comparator<String> CASE_INSENSITIVE_COMPARATOR = new Comparator<String>() {
		@Override
		public int compare(String left, String right) {
			// null goes to bottom when doing ascending order
			Integer nullVal = nullCompare(left, right);
			if (nullVal != null) {
				return nullVal;
			}

			// try to do numeric compare
			final Number leftNum = StrUtils.getNumber(left);
			final Number rightNum = StrUtils.getNumber(right);
			if (leftNum != null && rightNum != null) {
				return NUMERIC_COMPARATOR.compare(leftNum, rightNum);
			}

			// do a normal compare
			return String.CASE_INSENSITIVE_ORDER.compare(left, right);
		}
	};

	// note: something may exist that accomplishes this
	public static final Comparator<Number> NUMERIC_COMPARATOR = new Comparator<Number>() {
		@Override
		public int compare(Number left, Number right) {
			// push null to the bottom
			Integer nullVal = nullCompare(left, right);
			if (nullVal != null) {
				return nullVal;
			}

			if (left.equals(right)) {
				return 0;
			} else if (left.doubleValue() > right.doubleValue()) {
				return 1;
			} else {
				return -1;
			}
		}
	};

	public final static Comparator<Option> OPTION_NAME_COMPARATOR = new Comparator<Option>() {
		@Override
		public int compare(Option left, Option right) {
			if (left.getValue().toString().equals(BaseAction.NONE_OPTION)) {
				return -1;
			} else if (right.getValue().toString().equals(
					BaseAction.NONE_OPTION)) {
				return 1;
			}
			return CASE_INSENSITIVE_COMPARATOR.compare(left.getLabelAsString(),
					right.getLabelAsString());
		}
	};

	public final static Comparator<Option> OPTION_NAME_NUMERIC_COMPARATOR = new Comparator<Option>() {
		@Override
		public int compare(Option left, Option right) {
			return NUMERIC_COMPARATOR.compare((Integer) left.getValue(),
					(Integer) right.getValue());
		}
	};

	public final static Comparator<ColumnCheckbox> COLUMNCHECKBOX_LABEL_COMPARATOR = new Comparator<ColumnCheckbox>() {
		@Override
		public int compare(ColumnCheckbox left, ColumnCheckbox right) {
			return CASE_INSENSITIVE_COMPARATOR.compare(left.getLabel()
					.toString(), right.getLabel().toString());
		}
	};

	public final static Comparator<ColumnCheckbox> COLUMNCHECKBOX_DATATYPE_COMPARATOR = new Comparator<ColumnCheckbox>() {
		@Override
		public int compare(ColumnCheckbox left, ColumnCheckbox right) {
			return CASE_INSENSITIVE_COMPARATOR.compare(left.getDatatype(),
					right.getDatatype());
		}
	};

	public final static Comparator<Column> TABULAR_COLUMN_COMPARATOR = new Comparator<Column>() {
		@Override
		public int compare(Column left, Column right) {
			return NUMERIC_COMPARATOR
					.compare(left.getIndex(), right.getIndex());
		}
	};

	public final static Comparator<Column> TABULAR_COLUMN_NAME_COMPARATOR = new Comparator<Column>() {
		@Override
		public int compare(Column left, Column right) {
			return CASE_INSENSITIVE_COMPARATOR.compare(left.getName(), right
					.getName());
		}
	};

	public final static Comparator<Column> TABULAR_COLUMN_DATATYPE_COMPARATOR = new Comparator<Column>() {
		@Override
		public int compare(Column left, Column right) {
			return CASE_INSENSITIVE_COMPARATOR.compare(left.getDataType(),
					right.getDataType());
		}
	};

	public final static Comparator<Element> ELEMENT_SLICECOLUMN_COMPARATOR = new Comparator<Element>() {
		@Override
		public int compare(Element left, Element right) {
			// need to test if possible to do the cast
			SliceColumn leftColumn = (SliceColumn) left.getColumn();
			SliceColumn rightColumn = (SliceColumn) right.getColumn();

			return NUMERIC_COMPARATOR.compare(leftColumn.getOrderIndex(),
					rightColumn.getOrderIndex());
		}
	};

	public final static Comparator<ProblemGroup> PROBLEM_GROUP_COMPARATOR = new Comparator<ProblemGroup>() {
		@Override
		public int compare(ProblemGroup left, ProblemGroup right) {
			final int primaryCompare = NUMERIC_COMPARATOR.compare(left
					.getType().getSeverity().getValue(), right.getType()
					.getSeverity().getValue());
			if (primaryCompare != 0) {
				return primaryCompare;
			}
			return CASE_INSENSITIVE_COMPARATOR.compare(left.getDescription(),
					right.getDescription());
		}
	};

	public final static Comparator<ProblemCluster> PROBLEM_CLUSTER_COMPARATOR = new Comparator<ProblemCluster>() {
		@Override
		public int compare(ProblemCluster left, ProblemCluster right) {
			// comparing individual problems since cluster is suppressed
			if (left.getProblems().size() == 1
					&& right.getProblems().size() == 1) {
				return PROBLEM_COMPARATOR.compare(left.getProblems().get(0),
						right.getProblems().get(0));
			}
			// if one is a cluster and the other is a single problem, bubble the
			// cluster
			// TODO: consider if we want to do this or want to use lowest
			// filename and associated line number..
			if (left.getProblems().size() > 1
					&& right.getProblems().size() == 1) {
				return -1;
			}
			if (left.getProblems().size() == 1
					&& right.getProblems().size() > 1) {
				return 1;
			}
			// compare the descriptions
			final int primaryCompare = CASE_INSENSITIVE_COMPARATOR.compare(left
					.getDescription(), right.getDescription());
			return primaryCompare;
		}
	};

	public final static Comparator<DataSetProblem> PROBLEM_COMPARATOR = new Comparator<DataSetProblem>() {
		@Override
		public int compare(DataSetProblem left, DataSetProblem right) {
			int compare = CASE_INSENSITIVE_COMPARATOR.compare(left.getPath(),
					right.getPath());
			if (compare != 0) {
				return compare;
			}
			compare = NUMERIC_COMPARATOR.compare(left.getLineNumber(), right
					.getLineNumber());
			// if (compare != 0) {
			return compare;
			// }

			/*
			 * return CASE_INSENSITIVE_COMPARATOR.compare(left.getMessage(),
			 * right .getMessage());
			 */
		}
	};

	public final static Comparator<NewValue> NEW_VALUE_COMPARATOR = new Comparator<NewValue>() {
		@Override
		public int compare(NewValue left, NewValue right) {
			return CASE_INSENSITIVE_COMPARATOR.compare(left.getKey(), right
					.getKey());
		}
	};

	public final static Comparator<VolumeContainer> VOLUME_CONTAINER_COMPARATOR = new Comparator<VolumeContainer>() {
		@Override
		public int compare(final VolumeContainer left,
				final VolumeContainer right) {
			return CASE_INSENSITIVE_COMPARATOR.compare(left.getVolId(), right
					.getVolId());
		}
	};

	public final static Comparator<SetNode> SET_NODE_COMPARATOR = new Comparator<SetNode>() {
		@Override
		public int compare(final SetNode left, final SetNode right) {
			if (left.isDirectory() && !right.isDirectory()) {
				return -1;
			}
			if (!left.isDirectory() && right.isDirectory()) {
				return 1;
			}
			return CASE_INSENSITIVE_COMPARATOR.compare(left.getName(), right
					.getName());
		}
	};

	public final static Comparator<ExceptionContainer> EXCEPTION_CONTAINER_COMPARATOR = new Comparator<ExceptionContainer>() {
		@Override
		public int compare(final ExceptionContainer left,
				final ExceptionContainer right) {
			// NOTE: compare is reversed
			return NUMERIC_COMPARATOR.compare(right.getId(), left.getId());
		}
	};

	public static Integer nullCompare(Object left, Object right) {
		if (left == null) {
			if (right == null) {
				return 0;
			}
			return 1;
		} else if (right == null) {
			return -1;
		}
		return null;
	}

}
