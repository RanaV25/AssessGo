
// eagerly import theme styles so as we can override them
import "@vaadin/vaadin-lumo-styles/all-imports";

import "@vaadin/vaadin-charts/theme/vaadin-chart-default-theme";

const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `
<custom-style>
  <style>
html {
      --lumo-primary-color: hsla(214, 75%, 48%, 0.75);
      --lumo-error-color: hsla(3, 91%, 44%, 0.72);
      --lumo-success-color: hsla(145, 99%, 38%, 0.8);
      --lumo-primary-text-color: hsla(214, 75%, 48%, 0.75);
      --lumo-error-text-color: hsla(3, 91%, 44%, 0.72);
      --lumo-success-text-color: hsla(145, 99%, 38%, 0.8);
    }
  </style>
</custom-style>


<custom-style>
  <style>
    html {
      overflow:hidden;
    }
    vaadin-app-layout vaadin-tab a:hover {
      text-decoration: none;
    }
  </style>
</custom-style>

<dom-module id="app-layout-theme" theme-for="vaadin-app-layout">
  <template>
    <style>
      [part="navbar"] {
        align-items: center;
        justify-content: center;
      }
    </style>
  </template>
</dom-module>


//for custom tabs
//<dom-module id="tab" theme-for="vaadin-tab">
//    <template>
//        <style>
//
//            :host(.scrolling-tab) {
//                margin: 8px;
//                width: 200px;
//                height: 200px;
//                box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2);
//            }
//
//
//        </style>
//    </template>
//</dom-module>




<dom-module id="chart" theme-for="vaadin-chart">
  <template>
    <style include="vaadin-chart-default-theme">
      :host {
        --vaadin-charts-color-0: var(--lumo-primary-color);
        --vaadin-charts-color-1: var(--lumo-error-color);
        --vaadin-charts-color-2: var(--lumo-success-color);
        --vaadin-charts-color-3: var(--lumo-contrast);
      }
      .highcharts-container {
        font-family: var(--lumo-font-family);
      }
      .highcharts-background {
        fill: var(--lumo-base-color);
      }
      .highcharts-title {
        fill: var(--lumo-header-text-color);
        font-size: var(--lumo-font-size-xl);
        font-weight: 600;
        line-height: var(--lumo-line-height-xs);
      }
      .highcharts-legend-item text {
        fill: var(--lumo-body-text-color);
      }
      .highcharts-axis-title,
      .highcharts-axis-labels {
        fill: var(--lumo-secondary-text-color);
      }
      .highcharts-axis-line,
      .highcharts-grid-line,
      .highcharts-tick {
        stroke: var(--lumo-contrast-10pct);
      }
      .highcharts-column-series rect.highcharts-point {
        stroke: var(--lumo-base-color);
      }
    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
