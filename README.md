# sugarray

## Description

Android Network Library

## Usage

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
  });

// Get
SingleSugarray
  .init(context)
  .get(url)
  .set(headers)
  .query(querys)
  .end(new Sugarray.HttpResponseListener() {
    @Override
    public void preStart() {
      Log.d(TAG, "preStart");
    }
    @Override
    public void onSuccess(SugarrayResponse response) {
      Log.d(TAG, "onSuccess");
    }
    @Override
    public void onError(SugarrayError error) {
      Log.d(TAG, "onError");
    }
    @Override
    public void onFinish() {
      Log.d(TAG, "onFinish");
    }
  });

```

## Licence

MIT

## Author

[ultramagnetic](https://github.com/ultramagnetic-github)
