import { Configuration } from "../Configuration.js";

const uri = `${Configuration.protocol}://${Configuration.baseUrl}${Configuration.port ? ":" + Configuration.port : ""}${Configuration.path}api/playground/`;

export async function createPlayground(templateId) {
  try {
    const data = await fetch(`${uri}new`, {
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

export async function getPlaygroundHistory(playgroundId) {
  const data = await fetch(`${uri}${playgroundId}`, {
    method: "GET",
    headers: {
      "Access-Control-Allow-Origin": "*",
      "Content-Type": "application/json"
    }
  });
  return data.json();
}


const playgroundService = {
  createPlayground
};

export default playgroundService;