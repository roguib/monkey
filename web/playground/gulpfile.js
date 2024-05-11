const { argv } = require('yargs');
const fs = require('node:fs');

const ConfigurationJs = (baseUrl, port) => `
class Configuration {
  static get baseUrl() {
    return "${baseUrl}";
  }

  static get port() {
    return "${port}";
  }
}

export { Configuration };
`;

function defaultTask(cb) {
  // get env variables
  const baseUrl = argv.baseUrl;
  const port = argv.port;

  // create Configuration.js file
  fs.writeFileSync(
    `${__dirname}/src/Configuration.js`,
    ConfigurationJs(baseUrl, port),
    { flag: 'w' }
  );

  // finish async task
  cb();
}

exports.default = defaultTask;