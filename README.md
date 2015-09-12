Twitterist.org - Backend
========================

This is the backend (API) part of the [twitterist](twitterist.org) app. I provides a JSON API to load training sets 
and run predictions.

Usage
-----

Check out the repository from git and use activator to run the app:

```
./activator clean run
```

There is an interactive API-documentation in-app. Just open [localhost:8080](http://localhost:8080) in your browser.


To use the project in production, use the dist command to create production artifact: 

```bash
./activator dist
```

Development
-----------

Start the Solr server (used to persist predictions):

```
./server/solr/bin/solr start -f -p 8983
```

Tests
-----

To run the tests, use the following command:

```bash
./activator clean test
```

Contribution
------------

If you find any bug or have feedback please submit an issue on the 
[issue tracker](https://github.com/twitterist/backend/issues). Pull-Requests are also very welcome. Please write tests 
for your PR's. Otherwise, we cannot accept it.

License
-------

This project is licensed under MIT. See [LICENSE](LICENSE) for details.