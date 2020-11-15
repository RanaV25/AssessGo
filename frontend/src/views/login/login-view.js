import {html, PolymerElement} from '@polymer/polymer/polymer-element.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-login/src/vaadin-login-form.js';
import '@vaadin/vaadin-button/src/vaadin-button.js';

class LoginView extends PolymerElement {

    static get template() {
        return html`
<style include="shared-styles">
                :host {
                    display: block;
                    height: 100%;
                }
            </style>
<vaadin-vertical-layout theme="margin" style="width: 100%; height: 100%; justify-content: center;">
 <vaadin-login-form id="userLoginForm" style="align-self: center;"></vaadin-login-form>
 <p style="align-self: center;">Don't have an account? 
  <vaadin-button theme="tertiary" id="btnSignUp">
    Sign Up 
  </vaadin-button></p>
</vaadin-vertical-layout>
`;
    }

    static get is() {
        return 'login-view';
    }

    static get properties() {
        return {
            // Declare your properties here.
        };
    }
}

customElements.define(LoginView.is, LoginView);
