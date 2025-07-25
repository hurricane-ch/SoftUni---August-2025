#!/bin/bash

#git checkout -b release-2.1.X develop
#./bump-version.bash # (./mvnw versions:set -DgenerateBackupPoms=false -DnewVersion=2.1.X-SNAPSHOT)
#git git commit -am "Bumped version number to 2.1.X"
#git checkout master
#git merge --no-ff release-2.1.X
#git tag -a 2.1.X
#git push
#git push origin 2.1.X
#git checkout develop
#git merge --no-ff release-2.1.X
#git push
#git branch -d release-2.1.X

current_version=$(./mvnw -q \
    -Dexec.executable=echo \
    -Dexec.args='${project.version}' \
    --non-recursive \
    exec:exec)

echo "current_version="$current_version

new_version=$current_version
echo -n "Enter new version (default: $new_version): " && read new_version_read && [ -n "$new_version_read" ] && new_version=$new_version_read

echo "new_version: " $new_version

git checkout develop
git checkout -b release-$new_version develop
./mvnw versions:set -DgenerateBackupPoms=false -DnewVersion=$new_version
#sed -i "s/$current_version/$new_version/g" Dockerfile
git commit -am "Bumped version number to $new_version"
git checkout master
git merge --no-ff release-$new_version
git tag -a $new_version -m $new_version
git push
git push origin $new_version
git checkout develop
git merge --no-ff release-$new_version
git push
git branch -d release-$new_version
