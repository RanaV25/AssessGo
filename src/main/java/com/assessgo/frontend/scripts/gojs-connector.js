function gojsInit(_obj,_id) {
      if (window.goSamples) goSamples();  // init for these samples -- you don't need to call this
      var $ = go.GraphObject.make;  // for conciseness in defining templates

      myDiagram =
        $(go.Diagram, "weaveeOrgChartDiv",  // the DIV HTML element
          {
            // Put the diagram contents at the top center of the viewport
            initialDocumentSpot: go.Spot.TopCenter,
            initialViewportSpot: go.Spot.TopCenter,
            // OR: Scroll to show a particular node, once the layout has determined where that node is
            //"InitialLayoutCompleted": function(e) {
            //  var node = e.diagram.findNodeForKey(28);
            //  if (node !== null) e.diagram.commandHandler.scrollToPart(node);
            //},
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

      // define Converters to be used for Bindings
      function theNationFlagConverter(nation) {
        return "https://www.nwoods.com/go/Flags/" + nation.toLowerCase().replace(/\s/g, "-") + "-flag.Png";
      }

      function theInfoTextConverter(info) {
        var str = "";
        if (info.title) str += info.title;
        if (info.headOf) str += "\n\nHead of: " + info.headOf;
        if (typeof info.boss === "number") {
          var bossinfo = myDiagram.model.findNodeDataForKey(info.boss);
          if (bossinfo !== null) {
            str += "\n\nReporting to: " + bossinfo.name;
          }
        }
        return str;
      }

      // define the Node template
      myDiagram.nodeTemplate =
        $(go.Node, "Auto",
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

            // the country flag
            $(go.Picture,
              {
                row: 0, column: 1, margin: 2,
                imageStretch: go.GraphObject.Uniform,
                alignment: go.Spot.TopRight
              },
              // only set a desired size if a flag is also present:
              new go.Binding("desiredSize", "nation", function() { return new go.Size(34, 26) }),
              new go.Binding("source", "nation", theNationFlagConverter)),
            // the additional textual information
            $(go.TextBlock,
              {
                row: 1, column: 0, columnSpan: 2,
                font: "12px Roboto, sans-serif"
              },
              new go.Binding("text", "", theInfoTextConverter))
          )  // end Table Panel
        );  // end Node

      // define the Link template, a simple orthogonal line
      myDiagram.linkTemplate =
        $(go.Link, go.Link.Orthogonal,
          { corner: 5, selectable: false },
          $(go.Shape, { strokeWidth: 2, stroke: "#424242" }));  // dark gray, rounded corner links

      // create the Model with data for the tree, and assign to the Diagram
      myDiagram.model =
        $(go.TreeModel,
          {
            nodeParentKeyProperty: "parent",  // this property refers to the parent node data
            nodeDataArray: _obj
          });

    //Disable diagram clicks
    myDiagram.isEnabled = false;

       setTimeout(function(){ select(_id)},1000);
    }

    function select(_id)
    {
        myDiagram.startTransaction("highlight selected");
        var result = myDiagram.findNodeForKey(_id);
        if(result != null)
        {
            myDiagram.select(result);
            myDiagram.centerRect(result.actualBounds);
        }
        else
        {
             myDiagram.clearHighlighteds();
        }

         myDiagram.commitTransaction("highlight selected");
    }
