'use strict';

var NQPObject = require('./nqp-object.js');

class NQPInt extends NQPObject {
  constructor(value) {
    super();
    this.value = value | 0;
  }

  Str(ctx, _NAMED, self) {
    return this.value.toString();
  }

  $$toBool(ctx) {
    return (this.value ? 1 : 0);
  }

  $$numify() {
    return this.value;
  }
};

module.exports = NQPInt;
