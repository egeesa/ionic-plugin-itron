var exec = require('cordova/exec');

module.exports.openConnection = function (arg0, success, error) {
	exec(success, error, 'ItronBridge', 'openConnection', [arg0]);
};

module.exports.closeConnection = function (arg0, success, error) {
	exec(success, error, 'ItronBridge', 'closeConnection', [arg0]);
};

module.exports.readCyble = function (arg0, success, error) {
	exec(success, error, 'ItronBridge', 'readCyble', [arg0]);
};

module.exports.readCybleEnhanced = function (arg0, success, error) {
	exec(success, error, 'ItronBridge', 'readCybleEnhanced', [arg0]);
};

module.exports.readCyblePolling = function (arg0, success, error) {
	exec(success, error, 'ItronBridge', 'readCyblePolling', [arg0]);
};

module.exports.readPulsePolling = function (arg0, success, error) {
	exec(success, error, 'ItronBridge', 'readPulsePolling', [arg0]);
};

module.exports.readPulse = function (arg0, success, error) {
	exec(success, error, 'ItronBridge', 'readPulse', [arg0]);
};

module.exports.readPulseEnhanced = function (arg0, success, error) {
	exec(success, error, 'ItronBridge', 'readPulseEnhanced', [arg0]);
};

module.exports.updateLicense = function (arg0, success, error) {
	exec(success, error, 'ItronBridge', 'updateLicense', [arg0]);
};

module.exports.configureEnhancedDateAndTime = function (arg0, success, error) {
	exec(success, error, 'ItronBridge', 'configureEnhancedDateAndTime', [arg0]);
};

module.exports.configureDateAndTime = function (arg0, success, error) {
	exec(success, error, 'ItronBridge', 'configureDateAndTime', [arg0]);
};