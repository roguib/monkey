const { argv } = require('yargs');
const fs = require('node:fs');

const ConfigurationJs = (baseUrl, port, path) => `
class Configuration {
  static get baseUrl() {
    return "${baseUrl}";
  }

  static get port() {
    return "${port}";
  }

  static get path() {
    return "${path}";
  }
}

export { Configuration };
`;

function defaultTask(cb) {
  // get env variables
  const baseUrl = argv.baseUrl;
  const port = argv.port;
  const path = argv.baseUrlPath;

  console.log('=============GULP VARIABLES=============');
  console.log(baseUrl);
  console.log(port);
  console.log(path);

  // create Configuration.js file
  fs.writeFileSync(
    `${__dirname}/src/Configuration.js`,
    ConfigurationJs(baseUrl, port),
    { flag: 'w' }
  );

  console.log('=============GENERATED CONFIGURATION.JS FILE=============');
  console.log(ConfigurationJs(baseUrl, port, path));

  // finish async task
  cb();
}

exports.default = defaultTask;