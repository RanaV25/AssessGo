import {html, PolymerElement} from '@polymer/polymer/polymer-element.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/vaadin-button/src/vaadin-button.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/vaadin-icons/vaadin-icons.js';
import '@vaadin/vaadin-select/src/vaadin-select.js';

class PaginationComponent extends PolymerElement {

    static get template() {
        return html`
<style include="shared-styles">
                :host {
                    display: block;
                    height: 100%;
                }
            </style>
<vaadin-horizontal-layout theme="margin" style="width: 100%; height: 100%; justify-content: space-between;">
 <vaadin-select id="ddPageSize"></vaadin-select>
 <vaadin-horizontal-layout theme="" style="margin-right: 16px;">
  <vaadin-button theme="icon-large" aria-label="Add new" id="btnFirstPage">
   <iron-icon icon="vaadin:angle-double-left"></iron-icon>
  </vaadin-button>
  <vaadin-button theme="icon-large" aria-label="Add new" id="btnPreviousPage">
   <iron-icon icon="vaadin:angle-left"></iron-icon>
  </vaadin-button>
  <vaadin-button theme="icon-large" aria-label="Add new" id="btnNextPage">
   <iron-icon icon="vaadin:angle-right"></iron-icon>
  </vaadin-button>
  <vaadin-button theme="icon-large" aria-label="Add new" id="btnLastPage">
   <iron-icon icon="vaadin:angle-double-right"></iron-icon>
  </vaadin-button>
 </vaadin-horizontal-layout>
</vaadin-horizontal-layout>
`;
    }

    static get is() {
        return 'pagination-component';
    }

    static get properties() {
        return {
            // Declare your properties here.
        };
    }
}

customElements.define(PaginationComponent.is, PaginationComponent);
