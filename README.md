# sugarray

Android Network Library

## Description

## Demo

``` Java
// Get
SingleSugarray
  .init(context)
  .get(url)
  .set(headers)
  .query(querys)
  .end(new Sugarray.HttpResponseListener() {
    @Override
    public void onSuccess(SugarrayResponse response) {
        Log.d(TAG, response.stringfyBody());
    }
  })
```

## Licence

MIT

## Author

[ultramagnetic](https://github.com/ultramagnetic-github)
