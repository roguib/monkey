import { Configuration } from "../Configuration.js";

export async function fetchTemplates() {
  let templates = [];
  try {
    const uri = `${Configuration.protocol}://${Configuration.baseUrl}${Configuration.port ? ":" + Configuration.port : ""}${Configuration.path}`;
    const data = await fetch(`${uri}templates`, {
      method: "GET",
      headers: {
        "Access-Control-Allow-Origin": "*",
      }
    });
    if (!data.ok) {
      // no-op
    }
    templates = (await data.json())?.templates;
    if (!templates.length) {
      // TODO: Show alert an error has happened while loading templates
    }
  } catch (error) {
    // no-op
  }
  return templates;
}


const templateService = {
  fetchTemplates
};

export default templateService;