import {PolymerElement} from '@polymer/polymer/polymer-element.js';
import {html} from '@polymer/polymer/lib/utils/html-tag.js';
import {GoJsCharts} from './gojs-diagrams.js';

import '@vaadin/vaadin-accordion/vaadin-accordion.js';
import '@vaadin/vaadin-lumo-styles/all-imports.js';
import 'gojs/release/go.js';

import '@polymer/iron-icon/iron-icon.js';
import '@polymer/iron-icons/iron-icons.js';

class DiagramsView extends PolymerElement {
    static get template() {
        return html`
        <style>
        .base-pane{
            width:100%;
            height:1200px;
            overflow:auto;
            background-color: #fafafa;
        }
        .editor-pane{
            padding:25px;
            height:100%;
        }
        .document-pane{
            padding:25px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
            background-color:#ffffff;
            height:100%;
        }
        .editor{
            padding:10px;
            height:100%;
        }
        .tool-title{
            font-size:10pt;
        }
        .tool-summary{
            font-size:12pt;
        }
        .drop-area{
            background-color:#cccccc;
            border: 2px dotted #333333;
        }
        .chart-base{
            background-color:whitesmoke;
            width:100%;
            height:300px;
        }
        iron-icon{
            height:14px;
        }
        </style>
        <vaadin-horizontal-layout theme="" id="vaadinHorizontalLayout" style="width: 100%">
            <div id='tool-palette' style='border: none; width:20%; height:800px;'>
                <vaadin-accordion>
                  <vaadin-accordion-panel theme="filled">
                    <div slot="summary" class="tool-summary">Text</div>
                    <div id="tool-pallete-text" class="tool-title"><iron-icon icon="vaadin:font"></iron-icon>Rich Content</div>
                  </vaadin-accordion-panel>
                  <vaadin-accordion-panel theme="filled">
                    <div slot="summary" class="tool-summary">Graphs</div>
                    <div id="tool-pallete-chart" class="tool-title"><iron-icon icon="vaadin:chart-grid"></iron-icon>Charts</div>
                  </vaadin-accordion-panel>
                </vaadin-accordion>
            </div>
            <div class="base-pane">
                <div class="editor-pane">
                    <div class="document-pane">
                        <vaadin-vertical-layout class="editor" id="editor"></vaadin-vertical-layout>
                    </div>
                </div>
            </div>
        </vaadin-horizontal-layout>`;
     }

    static get is() {
       return 'diagrams-view';
    }

   static get properties() {
     return {
        uData: {
             type: Array,
             value: function() {
               return [];
             }
           }
     };
   }

   static get observers() {
       return [];
   }

   ready(){
       super.ready();
       this.style = "width: 100%;";
   }

   connectedCallback() {
     super.connectedCallback();
   }

   _attachDom(dom) {
      this.appendChild(dom);
   }

   afterServerUpdate(){
       var obj = JSON.parse(this.data);
       console.log(this.data);
       console.log(this.chartId);
       if(obj != null)
       {
            this.chart = new GoJsCharts(obj.object,this.chartId);
       }
   }

   goJs(_obj,_id) {
        var $ = go.GraphObject.make;  // for conciseness in defining templates
        if(this.chartsDiagram == null)
        {
         this.chartsDiagram =
          $(go.Diagram, _id,  // the DIV HTML element
            {
              // Put the diagram contents at the top center of the viewport
              initialDocumentSpot: go.Spot.TopCenter,
              initialViewportSpot: go.Spot.TopCenter,
              "clickCreatingTool.archetypeNodeData": { // allow double-click in background to create a new node
                name: "(new person)",
                title: "",
                key: ""
              },
              "undoManager.isEnabled": true,
              layout:
                $(go.TreeLayout,  // use a TreeLayout to position all of the nodes
                  {
                    treeStyle: go.TreeLayout.StyleLastParents,
                    // properties for most of the tree:
                    angle: 90,
                    layerSpacing: 80,
                    // properties for the "last parents":
                    alternateAngle: 0,
                    alternateAlignment: go.TreeLayout.AlignmentStart,
                    alternateNodeIndent: 20,
                    alternateNodeIndentPastParent: 1,
                    alternateNodeSpacing: 20,
                    alternateLayerSpacing: 40,
                    alternateLayerSpacingParentOverlap: 1,
                    alternatePortSpot: new go.Spot(0.001, 1, 20, 0),
                    alternateChildPortSpot: go.Spot.Left
                  })
            });

          var _this = this;
          // define the Node template
          this.chartsDiagram.nodeTemplate =
            $(go.Node, "Auto",
              {
                doubleClick: function(e, obj){
                 _this.showPanel();
                 var clicked = obj.part;
                 if(clicked != null)
                 {
                    _this.sendData(clicked.data);
                 }
              }},
              { click: function(e, obj){
              }},
              // the outer shape for the node, surrounding the Table
              $(go.Shape, "Rectangle",
                { fill: $(go.Brush, "Linear", { 0: "white", 1: "lightblue" }),
                          stroke: "darkblue", strokeWidth: 1 },
                /* reddish if highlighted, blue otherwise */
                new go.Binding("fill", "isSelected", function(h) { return h ? $(go.Brush, "Linear", { 0: "white", 1: "lightgreen" }) : $(go.Brush, "Linear", { 0: "white", 1: "lightblue" }); }).ofObject()),
              // a table to contain the different parts of the node
              $(go.Panel, "Table",
                { margin: 5, maxSize: new go.Size(150, NaN)},
                // the two TextBlocks in column 0 both stretch in width
                // but align on the left side
                $(go.RowColumnDefinition,
                  {
                    column: 0,
                    stretch: go.GraphObject.Horizontal,
                    alignment: go.Spot.Left
                  }),
                // the name
                $(go.TextBlock,
                  {
                    row: 0, column: 0,
                    maxSize: new go.Size(140, NaN), margin: 2,
                    font: "bold 16px Roboto, sans-serif",
                    alignment: go.Spot.Top
                  },
                  new go.Binding("text", "name")),
                // the additional textual information
                $(go.TextBlock,
                  {
                    row: 1, column: 0, columnSpan: 2,
                    font: "12px Roboto, sans-serif"
                  },
                  new go.Binding("text","description"))
              )  // end Table Panel
            );  // end Node


        //Define context menu to add/remove nodes
        this.chartsDiagram.nodeTemplate.contextMenu =
          $("ContextMenu",
            $("ContextMenuButton",
              $(go.TextBlock, "Add node"),
              {
                click: function(e, obj){_this.addNewNode(e, obj); }
              }
            ),
             $("ContextMenuButton",
               $(go.TextBlock, "Edit node"),
               {
                    click: function(e, obj){
                         _this.showPanel();
                         var clicked = obj.part;
                         if(clicked != null)
                         {
                            //_this.sendData(clicked.data);
                         }
                    }
                 }
             ),
            $("ContextMenuButton",
              $(go.TextBlock, "Delete node"),
              {
                click: function(e, obj){_this.deleteSelectedNode(e, obj); }
              }
            )
          );
          // define the Link template, a simple orthogonal line
          this.chartsDiagram.linkTemplate =
            $(go.Link, go.Link.Orthogonal,
              { corner: 5, selectable: false },
              $(go.Shape, { strokeWidth: 2, stroke: "#424242" }));  // dark gray, rounded corner links
          // create the Model with data for the tree, and assign to the Diagram
          this.chartsDiagram.model =
            $(go.TreeModel,
              {
                nodeKeyProperty: "id",
                nodeParentKeyProperty: "parent",  // this property refers to the parent node data
                nodeDataArray: _obj
              });

          this.chartsDiagram.model.addChangedListener(function(evt) {
              // ignore unimportant Transaction events
              if (!evt.isTransactionFinished) return;
              var data = _this.chartsDiagram.model.toIncrementalData(evt);
              if(data && data.modifiedNodeData)
              {
                  //_this.push("uData",data.modifiedNodeData);
              }
            });
            //Disable diagram clicks
            this.chartsDiagram.isEnabled = true;
        }
        else
        {
            this.chartsDiagram.model.nodeDataArray = _obj;
        }
      }

   select(_id){
          this.chartsDiagram.startTransaction("highlight selected");
          var result = this.chartsDiagram.findNodeForKey(_id);
          if(result != null)
          {
              this.chartsDiagram.select(result);
              this.chartsDiagram.centerRect(result.actualBounds);
          }
          else
          {
               this.chartsDiagram.clearHighlighteds();
          }

           this.chartsDiagram.commitTransaction("highlight selected");
      }

   deleteSelectedNode(e, obj){
            var clicked = obj.part;
             if (clicked !== null) {
               var thisemp = clicked.data;
               var _node = this.chartsDiagram.findNodeForKey(thisemp.id);
               this.chartsDiagram.model.commit(function(m) {  // this Model
                   m.set(_node.data, "status", -1);
                 }, "delete node");
             }
       }

   addNewNode(e, obj) {
          var clicked = obj.part;
          if (clicked !== null) {
            var thisemp = clicked.data;
            this.chartsDiagram.model.startTransaction("Add node");
            var newemp = {
              id:"",
              name: "[Title]",
              description: "[Description]",
              parent: thisemp.id,
              status: 1 //New
            };
            this.chartsDiagram.model.addNodeData(newemp);
            this.chartsDiagram.model.commitTransaction("Add node");
          }
    }
}

customElements.define(DiagramsView.is, DiagramsView);