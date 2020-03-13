cordova.define("cordova-plugin-itronbridge.ItronBridge", function(require, exports, module) {
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
    
        module.exports.readCyblePolling = function (arg0, success, error) {
            exec(success, error, 'ItronBridge', 'readCyblePolling', [arg0]);
        };
    
        module.exports.readPulse = function (arg0, success, error) {
            exec(success, error, 'ItronBridge', 'readPulse', [arg0]);
        };
    
        module.exports.updateLicense = function (arg0, success, error) {
            exec(success, error, 'ItronBridge', 'updateLicense', [arg0]);
        };
    
    
    });
    