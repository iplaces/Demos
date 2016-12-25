/**
 * Created by iplace on 2016/12/20.
 */
import { connect} from 'react-redux';
import Link from '../components/Footer/Link';
import { setVisibilityFilter } from '../actions';

const mapStateToProps = (state, ownProps) => {
    return {
        active: ownProps.filter === state.visibilityFilter
    }
};

const mapDispatchToProps = (dispatch, ownProps) => {
    return {
        onClick: () => {
            dispatch(setVisibilityFilter(ownProps.filter))
        }
    }
};

const FilterLink = connect(
    mapStateToProps,
    mapDispatchToProps
)(Link);

export default FilterLink;

