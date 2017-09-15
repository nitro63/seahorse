/**
 * Copyright (c) 2015, CodiLime Inc.
 *
 * Owner: Grzegorz Swatowski
 */

'use strict';

let SingleColumnSelectorItem = require('./common-single-column-selector-item.js');
let ColumnListSelectorItem = require('./common-column-list-selector-item.js');
let SingleIndexSelectorItem = require('./common-single-index-selector-item.js');
let IndexListSelectorItem = require('./common-index-list-selector-item.js');
let TypeListSelectorItem = require('./common-type-list-selector-item.js');

let selectorItemConstructors = {
  'column': SingleColumnSelectorItem,
  'columnList': ColumnListSelectorItem,
  'index': SingleIndexSelectorItem,
  'indexList': IndexListSelectorItem,
  'typeList': TypeListSelectorItem
};

let SelectorItemFactory = {
  createItem(value) {
    if (_.isUndefined(value) || _.isNull(value)) {
      return null;
    } else {
      let Constructor = selectorItemConstructors[value.type];
      return Constructor ?
        new Constructor({'item': value}) :
        null;
    }
  },
  getAllItemsTypes() {
    return {
      'singleSelectorItems': [
        SingleColumnSelectorItem.getType(),
        SingleIndexSelectorItem.getType()
      ],
      'multipleSelectorItems': [
        ColumnListSelectorItem.getType(),
        IndexListSelectorItem.getType(),
        TypeListSelectorItem.getType()
      ]
    };
  }
};

module.exports = SelectorItemFactory;
