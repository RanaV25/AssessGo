import {PolymerElement} from '@polymer/polymer/polymer-element.js';
import {html} from '@polymer/polymer/lib/utils/html-tag.js';

class TitleCard extends PolymerElement {
    static get template() {
        return html`
        <style>
        .title{
           padding:3px;
           float:left;
        }
        .close{
           padding:3px;
           float:right;
        }
        </style>
        <vaadin-vertical-layout theme="" id="cardbase" style="width: 100%">
            <vaadin-horizontal-layout theme="" id="titlebase" style="width: 100%">
                <div class="title"></div>
                <div id="close" class="close"><iron-icon icon="vaadin:close"></div>
            </vaadin-horizontal-layout>
            <vaadin-vertical-layout theme="" id="cmpbase" style="width: 100%">
            </vaadin-vertical-layout>
        </vaadin-vertical-layout>`;
     }
}

customElements.define(TitleCard.is, TitleCard);