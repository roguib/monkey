const { argv } = require('yargs');
const fs = require('node:fs');

const ConfigurationJs = (protocol, baseUrl, port, path, websocketProtocol) => `
class Configuration {
  static get protocol() {
    return "${protocol}";
  }

  static get baseUrl() {
    return "${baseUrl}";
  }

  static get port() {
    return "${port}";
  }

  static get path() {
    return "${path}";
  }

  static get websocketProtocol() {
    return "${websocketProtocol}";
  }
}

export { Configuration };
`;

function defaultTask(cb) {
  // get env variables
  const protocol = argv.baseUrlProtocol;
  const baseUrl = argv.baseUrlDomain;
  const port = argv.baseUrlPort;
  const path = argv.baseUrlPath;
  const websocketProtocol = argv.websocketProtocol;

  console.log('=============GULP VARIABLES=============');
  console.log(protocol);
  console.log(baseUrl);
  console.log(port);
  console.log(path);
  console.log(websocketProtocol);

  const configFile = ConfigurationJs(protocol, baseUrl, port, path, websocketProtocol);

  // create Configuration.js file
  fs.writeFileSync(
    `${__dirname}/src/Configuration.js`,
    configFile,
    { flag: 'w' }
  );

  console.log('=============GENERATED CONFIGURATION.JS FILE=============');
  console.log(configFile);

  // finish async task
  cb();
}

exports.default = defaultTask;