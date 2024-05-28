import { Configuration } from "../Configuration.js";

export async function createPlayground(templateId) {
  try {
    const uri = `${Configuration.protocol}://${Configuration.baseUrl}${Configuration.port ? ":" + Configuration.port : ""}${Configuration.path}`;
    const data = await fetch(`${uri}playground/new`, {
      method: "POST",
      headers: {
        "Access-Control-Allow-Origin": "*",
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        templateId
      })
    });
    if (!data.ok) {
      // no-op
    }
    const { id } = await data.json();
    return id;
  } catch (error) {
    // no-op
  }
  return "";
}


const playgroundService = {
  createPlayground
};

export default playgroundService;