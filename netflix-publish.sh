#!/bin/bash
# This is a Netflix-only script to do publishing into our Spinnaker installation.
# An OSS-friendly script could be written & added to Gradle if desired.

if [ "$#" -ne 1 ] && [ "$#" -ne 2 ]; then
  echo "usage: netflix-publish.sh VERSION [SPIN_ENV]"
  echo "VERSION     Version of the plugin (e.g. 1.10.0)"
  echo "SPIN_ENV    Spinnaker environment to publish to (defaults to prestaging)"
  exit 1
fi

SPIN_ENV="$2"
if [ "$SPIN_ENV" == "" ]; then
  SPIN_ENV="prestaging"
fi

if [ "$SPIN_ENV" == "main" ]; then
  SPIN_URL=https://api.spinnaker.mgmt.netflix.net
  FRONT50_URL=https://front50-main.spinnaker.mgmt.netflix.net
else
  SPIN_URL=https://api-$SPIN_ENV.spinnaker.mgmt.netflix.net
  FRONT50_URL=https://front50-$SPIN_ENV.spinnaker.mgmt.netflix.net
fi

VERSION="$1"
ARTIFACT=plugin-helloworld-$VERSION.zip

SHA=$(jq -r '.releases[].sha512sum' < build/distributions/plugin-info.json)
ID=$(jq -r '.id' < build/distributions/plugin-info.json)

jq ".releases[].url = \"$FRONT50_URL/pluginBinaries/$ID/$VERSION\"" build/distributions/plugin-info.json > build/distributions/plugin-info.tmp
mv build/distributions/plugin-info.tmp build/distributions/plugin-info.json

metatron curl -a gate -X POST -H "Content-Type: application/zip" \
  --data "@build/distributions/$ARTIFACT" \
  "$SPIN_URL:7004/plugins/upload/$ID/$VERSION?sha512sum=$SHA"
metatron curl -a gate -X POST -H "Content-Type: application/json" \
  --data "@build/distributions/plugin-info.json" \
  $SPIN_URL:7004/pluginInfo
