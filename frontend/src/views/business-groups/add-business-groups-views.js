import {PolymerElement} from '@polymer/polymer/polymer-element.js';
import {html} from '@polymer/polymer/lib/utils/html-tag.js';

import '@vaadin/vaadin-board/vaadin-board.js';
import '@vaadin/vaadin-charts/vaadin-chart.js';
import '@vaadin/vaadin-lumo-styles/all-imports.js';
import 'gojs/release/go.js';

class AddBusinessGroupsView extends PolymerElement {

 static get template() {
    return html`
    <vaadin-horizontal-layout theme="" id="vaadinHorizontalLayout" style="width: 100%">
        <div id='weaveeOrgChartDiv' style$='border: none; width:{{canvasWidth}}; height:800px;'></div>
        <vaadin-vertical-layout style$="width:{{containerWidth}};display:{{containerDisplay}};" id="mainContainer">
             <vaadin-form-layout id="userFormLayout" style="width: 100%; padding: 0px; height:100%; flex-grow: 0; margin-top: 0">
                 <vaadin-text-field error-message="" invalid="" label="Name" id="txtName"></vaadin-text-field>
                 <vaadin-text-area error-message="" invalid="" label="Description" id="txtDescription"></vaadin-text-area>
                 <vaadin-horizontal-layout theme="margin">
                  <vaadin-button theme="primary" id="btnAdd">
                    Save
                  </vaadin-button>
                  <vaadin-button theme="primary error" id="btnCancel" style="margin-left: 8px;" on-click="cancelAction">
                    Cancel
                  </vaadin-button>
                 </vaadin-horizontal-layout>
            </vaadin-form-layout>
        </vaadin-vertical-layout>
    </vaadin-horizontal-layout>`;
 }

 static get is() {
    return 'add-business-groups-view';
 }

  static get properties() {
    return {
       uData: {
            type: Array,
            value: function() {
              return [];
            }
          },
       canvasWidth: {
            type: String,
            value: '100%'
       },
       containerWidth: {
           type: String,
           value: '0%'
        },
       containerDisplay: {
           type: String,
           value: 'none'
       }
    };
  }

  static get observers() {
      return [
        '_modelUpdated(uData.*)'
      ];
  }

  ready(){
    super.ready();
    this.style = "width: 100%;";
  }

  cancelAction(e, detail){
    this.canvasWidth = "100%";
    this.containerWidth = "0%";
    this.containerDisplay = "none";
  }

  showPanel(){
    console.log("Show");
    this.canvasWidth = "70%";
    this.containerWidth = "30%";
    this.containerDisplay = "";
  }

  _modelUpdated(changes){
  var _this = this;
    if(changes){
    console.log(changes);
        if(changes.path == 'uData.splices')
        {
            changes.value.indexSplices.forEach(function(s) {
                                    s.removed.forEach(function(user) {
                                      console.log(user.name + ' was removed');
                                    });
                                    for (var i=0; i<s.addedCount; i++) {
                                      var index = s.index + i;
                                      var newUser = s.object[index];
                                      console.log('User ' + newUser[0].name + ' added at index ' + index);
                                      _this.sendData(newUser[0]);
                                    }
                                  }, this);
        }
    }
  }

  _attachDom(dom) {
    this.appendChild(dom);
  }

  afterServerUpdate(){
    var obj = JSON.parse( this.data);
    this.gojs( obj.object,-1);
  }

  sendData(data){
    console.log(data);
    this.$server.setData(JSON.stringify(data));
  }

  connectedCallback() {
      super.connectedCallback();
      console.log("callback" + this.name + " : " + this.description );
    }

    disconnectedCallback() {
      super.disconnectedCallback();
      clearInterval(this.timerId);
    }

  _print(s){
   console.log("Printing : " + s);
  }

  gojs(_obj,_id) {
        var $ = go.GraphObject.make;  // for conciseness in defining templates
        if(this.myDiagram == null)
        {
         this.myDiagram =
          $(go.Diagram, "weaveeOrgChartDiv",  // the DIV HTML element
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
          this.myDiagram.nodeTemplate =
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
        this.myDiagram.nodeTemplate.contextMenu =
          $("ContextMenu",
            $("ContextMenuButton",
              $(go.TextBlock, "Add business group"),
              {
                click: function(e, obj){_this.addNewBusinessGroup(e, obj); }
              }
            ),
             $("ContextMenuButton",
               $(go.TextBlock, "Edit business group"),
               {
                    click: function(e, obj){
                         _this.showPanel();
                         var clicked = obj.part;
                         if(clicked != null)
                         {
                            _this.sendData(clicked.data);
                         }
                    }
                 }
             ),
            $("ContextMenuButton",
              $(go.TextBlock, "Delete business group"),
              {
                click: function(e, obj){_this.deleteSelectedBusinessGroup(e, obj); }
              }
            )
          );
          // define the Link template, a simple orthogonal line
          this.myDiagram.linkTemplate =
            $(go.Link, go.Link.Orthogonal,
              { corner: 5, selectable: false },
              $(go.Shape, { strokeWidth: 2, stroke: "#424242" }));  // dark gray, rounded corner links

          if (window.Inspector) myInspector = new Inspector("myInspector", this.myDiagram,
          {
            properties: {
              "key": { readOnly: true },
              "comments": {}
            }
          });
          // create the Model with data for the tree, and assign to the Diagram
          this.myDiagram.model =
            $(go.TreeModel,
              {
                nodeKeyProperty: "id",
                nodeParentKeyProperty: "parent",  // this property refers to the parent node data
                nodeDataArray: _obj
              });

          this.myDiagram.model.addChangedListener(function(evt) {
              // ignore unimportant Transaction events
              if (!evt.isTransactionFinished) return;
              var data = _this.myDiagram.model.toIncrementalData(evt);
              if(data && data.modifiedNodeData)
              {
                  _this.push("uData",data.modifiedNodeData);
              }
            });
            //Disable diagram clicks
            this.myDiagram.isEnabled = true;
        }
        else
        {
            this.myDiagram.model.nodeDataArray = _obj;
        }
      }

  select(_id){
          this.myDiagram.startTransaction("highlight selected");
          var result = this.myDiagram.findNodeForKey(_id);
          if(result != null)
          {
              this.myDiagram.select(result);
              this.myDiagram.centerRect(result.actualBounds);
          }
          else
          {
               this.myDiagram.clearHighlighteds();
          }

           this.myDiagram.commitTransaction("highlight selected");
      }

  deleteSelectedBusinessGroup(e, obj){
            var clicked = obj.part;
             if (clicked !== null) {
               var thisemp = clicked.data;
               var _node = this.myDiagram.findNodeForKey(thisemp.id);
               this.myDiagram.model.commit(function(m) {  // this Model
                   m.set(_node.data, "status", -1);
                 }, "delete business group");
             }
       }

  addNewBusinessGroup(e, obj) {
          var clicked = obj.part;
          if (clicked !== null) {
            var thisemp = clicked.data;
            this.myDiagram.model.startTransaction("Add business group");
            var newemp = {
              id:"",
              name: "[Business Group Name]",
              description: "",
              parent: thisemp.id,
              status: 1 //New
            };
            this.myDiagram.model.addNodeData(newemp);
            this.myDiagram.model.commitTransaction("Add business group");
          }
    }

}

customElements.define(AddBusinessGroupsView.is, AddBusinessGroupsView);