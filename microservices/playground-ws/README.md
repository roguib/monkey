## Development
Builds playground-ws for development. Static sources have to be served using `react-scripts`. 

```bash
mvn clean package -Pdev
java -jar target/playground-ws.jar
```

If Chrome starts blocking requests due to CORS, open a new Chrome window with the following command that disables CORS:
```bash
open -na Google\ Chrome --args --user-data-dir=/tmp/temporary-chrome-profile-dir --disable-web-security
```

You'll also need to create a redis image

```bash
docker run --name monkey-redis -p 6379:6379 -d redis
```

And a postgres database
```bash
 docker run -itd -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=password -e POSTGRES_DB=playground -p 5432:5432 -v --name postgres
```

Make sure the values specified in `src/main/resources/application.yaml` point to the local running containers.

## Debug
If you use Idea, or any editor with remote debugging capabilities, the easiest option is to create a remote debugging configuration while running the jar containing the MP project.

Create a new configuration in your editor:
1. debugger mode: `Attach to remote JVM`
2. host: `localhost`
3. port: `5005`
4. `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005`

Run the jar and wait for the debugger process to attach to it.
```bash
 java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005 -jar target/playground-ws.jar
```

After the java process is running, go to your editor and attach the debugger.

## Release
Builds playground-ws and frontend sources for deployment.

In order to serve react static sources several modifications were needed.
1. `"homepage": "./web"` in `package.json`
2. Use `frontend-maven-plugin` to build react static resources
3. Use `maven-resources-plugin` to copy static resources into `target/classes/WEB/`. Notice that static resources have to be copied during `prepare-package` maven lifecycle. If copied before, they get overwritten after. For more information about maven lifecycle visit [introduction to maven lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#Plugins)
4. Create and endpoint that returns the `index.html` produced during react build

```bash
mvn clean package -Prelease
java -jar target/playground-ws.jar
```

Static sources are served in:
```
http://localhost:7001
```