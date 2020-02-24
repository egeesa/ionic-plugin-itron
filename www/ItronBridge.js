var exec = require('cordova/exec');

module.exports.send = function(arg0, success, error)
{
    exec(success, error, 'ItronBridge', 'send', [arg0]);
}
