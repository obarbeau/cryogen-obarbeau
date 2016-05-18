{:title "Moving Git repo to GitHub"
 :layout :post
 :tags  ["git" "github"]}

Today I pushed my [`repl-tasks`][1] project to GitHub,
whose repository was previously on a personal [Synology][2]
server.

```bash
# add the new repo on GitHub
git remote add new-origin git@github.com:obarbeau/repl-tasks.git
# push everything
git push --all new-origin
git push --tags new-origin
# rename old syno repo
git remote rename origin previous-on-syno
# rename new repo to master
git remote rename new-origin origin
# change destination of local master branch to new repo
git branch master --set-upstream-to=origin/master
```

The history is preserved.

If some branches had existed,
it would have been necessary to pull them first to the local repo and then push
them to the new one.

See this [post][3] for more information.

This project needs some refactoring and documentation, I'll do it ASAP...

[1]: https://github.com/obarbeau/repl-tasks|target=_blank
[2]: https://www.synology.com/|target=_blank
[3]: http://www.smashingmagazine.com/2014/05/19/moving-git-repository-new-server/|target=_blank
