var exec = require('cordova/exec');

module.exports.sendOpenBluetooth = function(arg0, success, error)
{
    exec(success, error, 'ItronBridge', 'sendOpenBluetooth', [arg0]);
}
