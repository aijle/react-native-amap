
var React = require('react');
var PropTypes = require('prop-types');

var ReactNative = require('react-native');
var {
  View,
  NativeMethodsMixin,
  requireNativeComponent,
  StyleSheet,
  ViewPropTypes
} = ReactNative;
var createClass = require('create-react-class');
var MapCallout = createClass({
  propTypes: {
    ...ViewPropTypes,
    tooltip: PropTypes.bool,
    onPress: PropTypes.func,
  },

  getDefaultProps: function() {
    return {
      tooltip: false,
    };
  },

  render: function() {
    return <AMapCallout {...this.props} style={[styles.callout, this.props.style]} />;
  },
});

var styles = StyleSheet.create({
  callout: {
    position: 'absolute',
    //flex: 0,
    //backgroundColor: 'transparent',
  },
});

var AMapCallout = requireNativeComponent('AMapCallout', MapCallout);

module.exports = MapCallout;
