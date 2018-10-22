        /**
	 * animation counter, 0 indicates start of animation, 1 means it's finished
	 */
	public int qi_animationStart;
	public String qi_boxHeader, qi_secondBoxHeader, qi_previousConstraint;
        var expdburl = location.protocol + "//" + location.host + "/expdb/";
	  
        PFont qi_font, qi_font2;

	  /**
	   * 3D driver
	   */
	  public Constraint qi_constraint;
	  public int qi_constraintCursor = 0;
	  public boolean qi_constraintEdited, qi_selectEdited;
	  
	  /**
	   * Drag stops the nodes from making tiny movements or vibrations. It makes
	   * sure the graph stops moving quickly after each adjustment.
	   */
	  public int qi_drag = 0;

          public float qi_scale = 1;

	  public boolean qi_dragging = false;

	  public boolean qi_drawsprings = false;

	  public boolean qi_help = false;

	  public int qi_helpAnimationStart;

	  public boolean qi_hideMarkers = true;

	  public boolean qi_hideRuns = true;

	  /**
	   * Float keeping the current height of a line of text
	   */
	  public float qi_lineHeight;
	  public int qi_listLength = 20;

	  public float qi_listOffset = 0;

	  public int qi_listWidth = 333;
	  public int qi_maxNrParticles = 20;

	  public float qi_oldX = 0;
	  public float qi_oldY = 0;
	  public float qi_paddingLeft = 1;
	  /**
	   * QSystem that holds all nodes and is connected to the physics engine.
	   */
	  public QSystem qi_physics;
	  public QNode qi_previousSelectedNode;

	  public String qi_query;
	  public ArrayList<ArrayList<String>> qi_rows = new ArrayList<ArrayList<String>>();
	  public int qi_scrollOffset = 0;

	  public String qi_searchString = "";
	  public String qi_select = null;

	  /**
	   * The currently selected node
	   */
	  public QNode qi_selectedNode;
	  public ArrayList<String> qi_selectionList = new ArrayList<String>();

	  /**
	   * Size of select/constraint markers
	   */
	  public float qi_selectSize = 24f;

	  /**
	   * States whether the select or constraint box of this node are visible
	   */
	  public boolean qi_selectVisible, qi_constraintVisible, qi_examplesVisible,
	      qi_startVisible, qi_initialized, qi_uploadVisible;

          public void hideTextBox(){
            qi_selectVisible = false;
            qi_constraintVisible = false;
            qi_examplesVisible = false;
            qi_constraint.hide();
          }

	  public boolean qi_waiting;
	  public boolean qi_highLight;

	  /**
	   * Strings for showing messages.
	   */
	  public String qi_warning = null;

	  public void addRow(ArrayList<String> row) {
	    qi_rows.add(row);
	  }

	  public void blueGraphics(boolean foreground) {
	    stroke(0, 0, 200, 120);
	    fill(200, 200, 255);
	  }

	  public void blueText(boolean foreground) {
	    stroke(0, 0, 200, 120);
	    fill(0, 0, 200);
	  }

	  public boolean boxTouched(float x, float listX) {
	    if (x >= listX) {
	      return true;
	    }
	    return false;
	  }

	  /**
	   * Generates an SQL query based on the graphical graph representation. It
	   * collects the selected attributes, gives meaningful aliases to each
	   * selected attribute, converts the links between nodes to table joins and
	   * includes all other constraints.
	   * 
	   * Does not yet allow aggregates, orderings or outer joins.
	   * 
	   * @return The SQL query (string) corresponding to the query graph. Returns
	   *         an empty string if query graph does not represent a valid query
	   *         (but will display a message).
	   */
	  public String buildQuery() {
	    // 0. Query graph sanity check
	    if (qi_physics.getTopNodes().size() > 1) {
	      setWarning("Confused: multiple graphs?");
	      return "";
	    } else {
	      // 1 Collect necessary nodes in query graph
	      ArrayList<String> selects = new ArrayList<String>();
	      ArrayList<String> groups = new ArrayList<String>();
	      ArrayList<String> constraints = new ArrayList<String>();
	      ArrayList<QNode> anodes = new ArrayList<QNode>();
	      ArrayList<QNode> nodes = new ArrayList<QNode>();
	      for (QNode q : qi_physics.getQNodes()){
	        if (q.isActive())
	          anodes.add(q);
	        if(q.getHiddenChild()!=null && q.getHiddenChild().isActive())
	          anodes.add(q.getHiddenChild());
	      }

	      // 2 Collect selections and constraints
	      for (QNode q : anodes) {
	        // 2.1 Collect selections
	        for (String s : q.getSelects()) {
	          String selectName = q.getAlias() + "." + s;
	          if (!nodes.contains(q))
	            nodes.add(q);
	          String agg = "";

	          // add group by fields to group list, continue with
	          // remainder
	          if (s.contains(" over ")) {
	            selectName = s.split(" over ")[0];
	            for (String group : s.split(" over ")[1].split(","))
	              if(!groups.contains(group))
	                groups.add(group);
	            s = selectName;
	          }
	          // insert default aggregation for crosstabulation
	          if (s.contains(" per ") && !s.contains("("))
	            agg = "group_concat";
	          // handle aggregates, store aggregate to agg string,
	          // continue...
	          if (s.contains("(")) {
	            agg = s.split("\\(")[0];
	            s = s.split("\\(")[1].split("\\)")[0];
	          }
	          // handle crosstabulations
	          if (s.contains(" per ")) {
	            String[] cs = s.split(" per ");
	            String[] crossTabField = cs[1].split("\\.");
	            // find node whose field we are crosstabulating against,
	            // add to node list
	            QNode q2 = null;
	            for (QNode qq : qi_physics.getAllNodes())
	              if (qq.getAlias().equals(crossTabField[0])) {
	                q2 = qq;
	              }
	            if (!nodes.contains(q2))
	              nodes.add(q2);
	            // collect all optional values: check constraints
	            ArrayList<String> options = new ArrayList<String>();
	            for (String constr : q2.getConstraints())
	              if (constr.startsWith(crossTabField[1]))
	                for (String co : constr.split(" or "))
	                  options.add(co.split(" = ")[1]);
	            // no constraints, retrieve all values
	            if (options.size() == 0) {
	              QNode qOld = qi_selectedNode;
	              qi_selectedNode = q2;
	              doValueQuery(crossTabField[1]);
	              runQuery(qi_query);
	              ArrayList<ArrayList<String>> rows = getRows();
	              //setWarning(null);
	              for (ArrayList<String> l : rows)
	                if (l.size() > 0 && l.get(0)!=null && l.get(0).length()>0)
	                  options.add(l.get(0));
	              qi_selectedNode = qOld;
	              qi_query = null;
	            }
	            // build select field for crosstabs
	            for (String option : options) {
	              selectName = agg + "(if(" + cs[1] + " = '" + option
	                  + "', " + q.getAlias() + "." + cs[0]
	                  + ", NULL)) as ";
	              if (agg.equals("group_concat"))
	                selectName += option.replace(".", "");
	              else
	                selectName += agg + "_"
	                    + option.replace(".", "");
	              selects.add(selectName);
	            }
	          }
	          // 2.1.1 Provide meaningful aliases for field 'value'
	          else if (s.contains("value")) {
	            boolean aliased = false;
	            QNode cn = null;
	            if (q.getFullName().equals("Evaluation"))
	              cn = q;
	            else if (q.getChild("Evaluation function") != null)
	              cn = q.getChild("Evaluation function");
	            else if (q.getFullName().equals("Input_Setting"))
	              cn = q.getChild("Parameter");
	            else if (q.getFullName().equals("Algorithm_Quality")
	                || q.getFullName().equals("Data_Quality"))
	              cn = q.getChild("Quality");
	            if (cn != null)
	              for (String constraint : cn.getConstraints())
	                if (constraint.startsWith("function")
	                    || constraint.startsWith("input")
	                    || constraint.toLowerCase().contains(
	                        "name")) {
	                  if (agg.length() > 0)
	                    s = agg
	                        + "("
	                        + q.getAlias()
	                        + "."
	                        + s
	                        + ") as "
	                        + agg
	                        + "_"
	                        + constraint.split(" = ")[1]
	                            .split(" ")[0].replace('.','_').replace('(','_').replace(')','_');
	                  else
	                    s = q.getAlias()
	                        + "."
	                        + s
	                        + " as "
	                        + constraint.split(" = ")[1]
	                            .split(" ")[0].replace('.','_').replace('(','_').replace(')','_');
	                  selects.add(s);
	                  aliased = true;
	                }
	            if (!aliased)// aliasing failed, just add selection
	              selects.add(q.getAlias() + "." + s);
	          } else if (agg.length() > 0)
	            selects.add(agg + "(" + q.getAlias() + "." + s + ")");
	          else
	            selects.add(q.getAlias() + "." + s);
	        }

	        // 2.2 Collect constraints
	        //System.out.println("Adding constraints for "+q.getFullName());
	        for (String s : q.getConstraints()) {
	          if (!nodes.contains(q))
	            nodes.add(q);
	          if (s.contains(" or "))
	            constraints.add("("
	                + q.getAlias()
	                + "."
	                + s.replace(" = ", " = '").replace(" or ",
	                    "' or " + q.getAlias() + ".") + "')");
	          else
	            constraints.add(q.getAlias() + "."
	                + s.replace(" = ", " = '") + "'");
	        }
	      }

	      // 2.3 Collect table joins (e.g. e.liid=li.liid)
	      for (QNode q : anodes)
	        if (!nodes.contains(q))
	          nodes.add(q);
	      for (QNode q : nodes) {
	        if (nodes.contains(q.getParent())) {
	          String[] conn = q.getConnector().split(":");
	          constraints.add(q.getParent().getAlias() + "." + conn[0]
	              + " = " + q.getAlias() + "." + conn[1]);
	        }
	      }

	      // 3 Write SQL query
              //single field select should have distinct function
              if(constraints.size()==0 && selects.size()==1)
                selects.set(0,"distinct "+selects.get(0));
	      String query = "";
	      if (selects.size() > 0) {
	        query = "SELECT ";
	        for (int i = 0; i < selects.size(); i++) {
	          if (i != 0)
	            query += ", ";
	          query += selects.get(i);
	        }
	        query += "\n";

	        query += "FROM ";
	        for (int i = 0; i < nodes.size(); i++) {
	          if (i != 0)
	            query += ", ";
	          query += nodes.get(i).getFullName().toLowerCase() + " "
	              + nodes.get(i).getAlias();
	        }
	        query += "\n";

	        if (constraints.size() > 0) {
	          query += "WHERE ";
	          for (int i = 0; i < constraints.size(); i++) {
	            if (i != 0)
	              query += " and ";
	            query += constraints.get(i);
	          }
	        }

	        if (groups.size() > 0) {
	          query += "\ngroup by ";
	          for (int i = 0; i < groups.size(); i++) {
	            if (i != 0)
	              query += ",";
	            query += groups.get(i);
	          }
	        }
	      }
	      //block unsafe queries
	      if((selects.contains("e.value") || selects.contains("e.function") || selects.contains("e.implementation") || selects.contains("e.label")) && constraints.size()<6){
	        setWarning("Query result too large, limiting to 1000 rows.");
	        query+=" limit 0,1000";
	      }
	      return query;
	    }
	  }

	  /**
	   * Returns the name of the button that was clicked.
	   * 
	   * @param x
	   *            X-coordinate of the click
	   * @param y
	   *            Y-coordinate of the click
	   * @return The name of the clicked button, null otherwise.
	   */
	  public String buttonClicked(float xx, float yy) {
	    if (qi_constraintVisible || qi_selectVisible) {
	      if (xx > scaleX(width - 50) && yy > scaleY(7)
	          && xx < scaleX(width - 3)
	          && yy < scaleY(7) + qi_lineHeight * 1.8f)
	        return "done";
	      else if (xx > scaleX(width - 110) && yy > scaleY(7)
	          && xx < scaleX(width - 53)
	          && yy < scaleY(7) + qi_lineHeight * 1.8f)
	        return "delete";
	      else
	        return null;
	    } 
       else if (qi_uploadVisible) {
        if (xx > scaleX(width - 50) && yy > scaleY(7)
            && xx < scaleX(width - 3)
            && yy < scaleY(7) + qi_lineHeight * 1.8f)
          return "save";
        else
          return null;
        } else {
	      float spacing = 1.5f * getSelectSize();
	      float moreSpacing = 0;
	      if (qi_help)
	        moreSpacing = 0.8f * getSelectSize();
	      float offset = scaleX(width) - getSelectSize();

	      if (pointWithinEllipse(offset, scaleY(2) + getSelectSize() / 2,
	          getSelectSize() / 2, getSelectSize() / 2, xx, yy))
	        return "upload";
	      else if (pointWithinEllipse(offset - (spacing + moreSpacing),
	          scaleY(2) + getSelectSize() / 2, getSelectSize() / 2,
	          getSelectSize() / 2, xx, yy))
	        return "download";
	      else if (pointWithinEllipse(offset - 2 * (spacing + moreSpacing),
	          scaleY(2) + getSelectSize() / 2, getSelectSize() / 2,
	          getSelectSize() / 2, xx, yy))
	        return "restart";
	      else if (pointWithinEllipse(offset - 3 * (spacing + moreSpacing),
	          scaleY(2) + getSelectSize() / 2, getSelectSize() / 2,
	          getSelectSize() / 2, xx, yy))
	        return "examples";
	      else if (xx > scaleX(40) && yy > scaleY(height - 16)
	          && xx < scaleX(80) && yy < scaleY(height))
	        return "help";
	      else
	        return null;
	    }
	  }

	  /**
	   * Checks whether the center of the graph has shifted considerably.
	   * 
	   * @return True if the center has shifted by more than 300 points, false
	   *         otherwise.
	   */
	  public boolean centroidUpdateNeeded() {
	    for (QNode q : qi_physics.getQNodes()) {
	      if (getCorrectedMaxPosition(q).x > 300
	          || getCorrectedMaxPosition(q).y > 300)
	        return true;
	    }
	    return false;
	  }

	  public String checkMarkers(float xx, float yy) {
	    // check whether a marker was touched
	    if (!qi_hideMarkers) {
	      float x = qi_selectedNode.getNodePosition().x;
	      float y = qi_selectedNode.getNodePosition().y;
	      float ax = getNodeWidth(qi_selectedNode) * 0.7f;
	      float ay = getNodeHeight(qi_selectedNode) * 0.8f;
	      float xd = (float) sqrt(ax * ax + ay * ay);
	      float r = getSelectSize() / 2;
	      // Expand
	      // if (pointWithinEllipse(x + ay, y - ax, r, r, xx, yy))
	      // return "expand";
	      // Aggregate
	      if (pointWithinEllipse(x + ax, y - ay, r, r, xx, yy))
	        return "expand";
	      // Select
	      if (pointWithinEllipse(x + xd, y, r, r, xx, yy))
	        return "select";
	      // Constraint
	      if (pointWithinEllipse(x + ax, y + ay, r, r, xx, yy))
	        return "constraint";
	      // Collapse
	      if (pointWithinEllipse(x + ay, y + ax, r, r, xx, yy))
	        return "collapse";
	    }
	    return null;
	  }

	  /**
	   * Returns the position of the constraints shown with this node.
	   * 
	   * @return A PVector representation of the coordinates
	   */
	  public PVector constraintPosition(QNode q) {
	    PVector p = q.getNodePosition();
	    return new PVector(p.x + getNodeWidth(q) / 2, p.y + getNodeHeight(q)
	        / 2, p.z);
	  }

	  /**
	   * Returns the constraint touched by coordinates xx and yy, or null
	   * otherwise.
	   * 
	   * @param xx
	   *            The X coordinate of the selection
	   * @param yy
	   *            The Y coordinate of the selection
	   * @param lineheight
	   *            The current text line height
	   * @return Returns -1 if no constraint was touched, otherwise the number of
	   *         the touched constraint number
	   */
	  public String constraintTouched(QNode q, float xx, float yy) {

	    float padding = qi_lineHeight / 2;
	    float x = constraintPosition(q).x + padding;
	    float y = constraintPosition(q).y - qi_lineHeight * 0.6f;

	    if (xx < x || yy < y)
	      return null;
	    float lineBase = 0;
	    for (int f = 0; f < q.getAllConstraints().size(); f++) {
	      String s = q.getAllConstraints().get(f);
	      ArrayList<String> ss = new ArrayList<String>();
	      if (s.length() > 0) {
	        int lines = int( ceil(s.length() / 50f));
	        float constraintWidth = 0;
	        for (int i = 0; i < lines; i++)
	          constraintWidth = max(
	              constraintWidth,
	              textWidth(s.substring(i * 50,
	                  min(49 + (i * 50), s.length())))
	                  + 2 * padding);
	        if (yy > y + lineBase
	            && yy < (y + lineBase + (lines + 0.5f) * qi_lineHeight)
	            && xx < x + constraintWidth)
	          return s;
	        lineBase += (lines + 0.5f) * qi_lineHeight;
	      }
	    }
	    return null;
	  }

	  public void doPropertyQuery() {
	    qi_selectionList = new ArrayList<String>();
	    QNode h = qi_selectedNode.getHiddenChild();
	    if (h != null)
	      qi_selectionList.addAll(h.getFields());
	    qi_selectionList.addAll(qi_selectedNode.getFields());
	  }

	  public void doValueQuery(String value) {
	    // tables involved in the query
	    ArrayList<QNode> tables = new ArrayList<QNode>();
	    // constraints of the query
	    ArrayList<String> constraints = new ArrayList<String>();

	    // find node to whom the given value belongs (it may be a hidden node)
	    QNode q = qi_selectedNode;
	    if (!q.getFields().contains(value)) {
	      QNode n = q.getHiddenChild();
	      if (n != null && n.getFields().contains(value))
	        q = n;
	    }
	    //System.out.println("doValueQuery: value belongs to " + q.getFullName());

	    // add constraints already added for this node
	    for (String c : q.getConstraints()) {
	      String constraint = "";
	      for (String cc : c.split(" or ")) {
	        if (!constraint.equals(""))
	          constraint += " or ";
	        String[] cs = cc.split(" = ");
	        // skip constraints about the values in question, add all others
	        if (!cs[0].equals(value)) {
	          constraint += q.getAlias() + "." + cs[0] + " = '" + cs[1]
	              + "'";
	        }
	      }
	      if (!constraint.equals("") && constraint!=null) {
	        if (constraint.contains(" or "))
	          constraints.add("(" + constraint + ")");
	        else
	          constraints.add(constraint);
	      }
	    }
	    // add role constraints if appropriate
	    if (q.getInstrinsicRole() != null)
	      constraints.add("role = '" + q.getInstrinsicRole() + "'");
	    // go up to see if there are additional constraints connected to parents
	    QNode parent = q.getParent();
	    QNode child = q;
	    while (parent != null) {
	      //System.out.println("Parent "+parent.getFullName());
	      // if parent has additional constraints, add connectors
	      if (child.hasParentWithConstraints()) {
	        tables.add(parent);
	        //System.out.println("Parent "+parent.getFullName()+" table added.");
	        String[] c = child.getConnector().split(":");
	        constraints.add(parent.getAlias() + "." + c[0] + " = "
	            + child.getAlias() + "." + c[1]);
	      }
	      // add constraints connected to parent node
	      for (String c : parent.getConstraints()) {
	        String[] cs = c.split(" = ");
	        constraints.add(parent.getAlias() + "." + cs[0] + " = '"
	            + cs[1] + "'");
	      }
	      // add parent role constraints if appropriate
	      if (parent.getInstrinsicRole() != null)
	        constraints.add(parent.getAlias() + "." + "role = '"
	            + parent.getInstrinsicRole() + "'");
	      child = parent;
	      parent = parent.getParent();
	    }

	    // add the table
	    tables.add(q);

	    // build query
	    value = q.getAlias() + "." + value;
	    String s = "select distinct " + value + " from ";
	    for (int i = 0; i < tables.size(); i++) {
	      if (i != 0)
	        s += ", ";
	      s += tables.get(i).getFullName().toLowerCase() + " " + tables.get(i).getAlias();
	    }
	    for (int i = 0; i < constraints.size(); i++) {
	      if (i == 0)
	        s += " where ";
	      else
	        s += " and ";
	      s += constraints.get(i);
	    }
	    s += " order by " + value;
	    qi_query = s;
	  }


          public void drawText(String mytext, float x, float y){
              pushMatrix();
              textFont(qi_font,14);
              text(mytext,x,y);
              popMatrix();          
          }
          
          public void drawSmallText(String mytext, float x, float y){
              pushMatrix();
              textFont(qi_font,12);
              text(mytext,x,y);
              popMatrix();          
          }
          
          public void drawSmallerText(String mytext, float x, float y){
              pushMatrix();
              textFont(qi_font,10);
              text(mytext,x,y);
              popMatrix();          
          }
          
	  /**
	   * Draws the query graph. Is called by the PApplet.
	   */
	  public void draw() {
            
	    //frameRate(1);
	    // Do a physics update before drawing if necessary
	    if (update() || qi_dragging || qi_drag < 20){
	      for (int i = 0; i < 3; i++) {
                qi_physics.tick(10);
                updateCentroid();
	      }
	    }
            else if(!qi_startVisible && !qi_selectVisible && !qi_constraintVisible && !qi_examplesVisible)
              noLoop();
            
            //updateCentroid();
            //qi_physics.tick(10);
	    qi_drag++;
 
	    // set background
	    background(255);
	    // update edge lengths
	    // center the graph
	    translate(width / 2, height / 2);
	    qi_lineHeight = (float) 12f;
	    qi_paddingLeft = (float) 5f;

	    // Draw the buttons, messages, warnings
	    drawButtons();
	    drawWarning();

	    // Draw the query graph
	    drawNetwork();
	    // Do querying if necessary. This is done as part of the draw cycle to
	    // control how it affects drawing.
	    
	    if (qi_query != null && !qi_waiting) {
	      loadList(qi_query);
	      qi_query = null;
	    }
	  }

	  /**
	   * Draws the 'buttons': examples, load, save, resend
	   */
  public void drawButtons() {
      float animationOffset = min(200, millis() - qi_helpAnimationStart) / 200f;
      //strokeJoin(ROUND);
      smooth();
      float spacing = 1.5f * getSelectSize();
      float moreSpacing = 0;
      if (qi_help)
        moreSpacing = 0.8f * getSelectSize() * animationOffset;
      float offset = scaleX(width) - getSelectSize();
      // distances
      float dx = getSelectSize() / 4.8f;
      float dy = getSelectSize() / 7;
      float h = getSelectSize() / 2.1f;

      strokeWeight(1.5f);
      grayGraphics(true);
      pushMatrix();
      // upload
      translate(offset, scaleY(2) + getSelectSize() / 2);
      ellipse(0, 0, getSelectSize(), getSelectSize());
      beginShape();
      vertex(-dx, dy);
      vertex(-dx, 2 * dy);
      vertex(dx, 2 * dy);
      vertex(dx, dy);
      endShape();
      beginShape();
      vertex(-dx, -dy);
      vertex(0, -2 * dy);
      vertex(dx, -dy);
      vertex(0, -2 * dy);
      vertex(0, dy);
      endShape();
      //strokeweight(scaleZ(1.5f));
      // download
      translate(-(spacing + moreSpacing), 0);
      ellipse(0, 0, getSelectSize(), getSelectSize());
      //strokeWeight(0.1f);
      beginShape();
      vertex(-dx, dy);
      vertex(-dx, 2 * dy);
      vertex(dx, 2 * dy);
      vertex(dx, dy);
      endShape();
      beginShape();
      vertex(-dx, 0);
      vertex(0, dy);
      vertex(dx, 0);
      vertex(0, dy);
      vertex(0, -2 * dy);
      endShape();
      //strokeweight(scaleZ(1.5f));
      // restart
      translate(-(spacing + moreSpacing), 0);
      ellipse(0, 0, getSelectSize(), getSelectSize());
      //strokeWeight(0.1f);
      //ellipse(0, 0, h, h);
      //stroke(200, 200, 200);
      //fill(200, 200, 200);
      //rect(-getSelectSize() / 3.7f, 0, getSelectSize() / 3.7f,
      //    getSelectSize() / 3.7f);
      //grayGraphics(true);
      arc(0, 0, h, h, 0.25*PI, 1.75*PI);
      translate(h/16, -h/16);
      fill(40, 40, 40);
      beginShape();
      vertex(h/2, -h/3);
      vertex(h/2, 0);
      vertex(h/6, 0);
      endShape(CLOSE);
      translate(-h/16, h/16);
      grayGraphics(true);
      //strokeweight(scaleZ(1.5f));
      // examples
      translate(-(spacing + moreSpacing), 0);
      ellipse(0, 0, getSelectSize(), getSelectSize());
      beginShape();
      float angle = TWO_PI / 10;
      for (int i = 0; i < 10; i++) {
        if (i % 2 == 0)
          vertex(cos(PI / 10 + angle * i) * 0.8f * dx, sin(PI / 10
              + angle * i)
              * 0.8f * dx);
        else
          vertex(cos(PI / 10 + angle * i) * 0.8f * h, sin(PI / 10 + angle
              * i)
              * 0.8f * h);
      }
      endShape(CLOSE);
      popMatrix();

      grayText(true);
      textAlign(CENTER, CENTER);
      if (qi_help) {
        pushMatrix();
        translate(offset, scaleY(35));
        drawSmallText("upload", 0,0);
        drawSmallText("download", - (spacing + moreSpacing), 0);
        drawSmallText("restart", - 2 * (spacing + moreSpacing), 0);
        drawSmallText("examples", - 3 * (spacing + moreSpacing), 0);
        popMatrix();
      }

      pushMatrix();
      // upload
      translate(scaleX(20), scaleY(height*0.97));
      drawText("Help", 0, 0);
      translate(12,-10);
      scale(12);
      strokeWeight(0.2f);
      ////textFont(verd11bol);
      float toggleWidth = 24;
      float toggleHeight = 12;
      //textFont(qi_font,12*scaleZ(1));
      if (qi_help) {
        rrect(0, 0, 3,1, 0.5, "blue",true);
        grayGraphics(true);// 77
        ellipse(1+animationOffset*2,1, 1, 1);
      } else {
        rrect(0, 0, 3,1, 0.5,"gray",true);
        ellipse(1+(1 - animationOffset)*2, 1, 1, 1);
      }
      scale(1/12);
        if (animationOffset == 1){
          if(qi_help){
          blueText(true);
          drawSmallerText("ON", 18, 12);}
          else{
          grayText(true);
          drawSmallerText("OFF", 29, 12);}
        }
      
      //textFont(qi_font,scaleZ(14));
      popMatrix();
    }
	  
	  /**
	   * Draws a gray dot on the given position.
	   * 
	   * @param x
	   *            Array of floats for X and Y coordinates.
	   */
	  public void drawDot(float... x) {
	    stroke(122, 80);
	    point(x[0], x[1]);
	  }

	  /**
	   * Draws the query graph.
	   */
	  public void drawNetwork() {
  
            if(qi_startVisible || qi_selectVisible || qi_constraintVisible || qi_examplesVisible)
              translate(-qi_listWidth/2,0);
              
	    // 1.0 draw edges
	    stroke(201, 181, 255, 180);
	    smooth();

	    for (QNode q : qi_physics.getQNodes()) {
	      if (q.getParent() != null) {
	        QNode p = q.getParent();

                
                beginShape();
	        if ((q.getType().startsWith("data") && p.getType().equals("run"))
	            || (q.getType().equals("run") && p.getType().startsWith(
	                "data")))
	          strokeWeight(3f);
	        else
	          strokeWeight(2f);

                vertex(q.getNodePosition().x, q.getNodePosition().y);
                vertex(p.getNodePosition().x, p.getNodePosition().y);
                endShape();

	        // 1.1 draw arrows
	        if (q.getType().startsWith("data") || q.getType().equals("run")) {
	          QNode a, b;
	          if (q.getType().equals("data:out")) {
	            a = p;
	            b = q;
	          } else {
	            a = q;
	            b = p;
	          }

                  strokeWeight(2f);
	          pushMatrix();
	          translate(
	              min(a.getNodePosition().x, b.getNodePosition().x)
	                  + abs(b.getNodePosition().x - a.getNodePosition().x)
	                  / 2,
	              min(a.getNodePosition().y, b.getNodePosition().y)
	                  + abs(b.getNodePosition().y - a.getNodePosition().y)
	                  / 2);
	          float angle = atan2(a.getNodePosition().x - b.getNodePosition().x,
	              b.getNodePosition().y - a.getNodePosition().y);
	          rotate(angle);
	          float arrowSize = 8;
                  beginShape();
                  vertex(-arrowSize,-arrowSize);
                  vertex(0,0);
                  endShape();
                  beginShape();
                  vertex(0,0);
                  vertex(arrowSize,-arrowSize);
                  endShape();
	          popMatrix();
	        }
	      }
	    }

	    // 2.0 draw nodes
      textFont(qi_font,14);
      for (QNode v : qi_physics.getQNodes()) {
        // draw nodes themselves, in different colors
        float x = v.getNodePosition().x;
        float y = v.getNodePosition().y;

        if (!v.isHidden() && !(v.getType().equals("run") && qi_hideRuns && !v.isHanging())) {
          String type = v.getType();
          stroke(201, 181, 255);
          fill(220, 220, 255);
          if (type.equals("run"))
            fill(255, 255, 255);
          else if (type.equals("setup"))
            fill(220, 220, 255);
          else if (type.startsWith("data"))
            fill(200, 255, 200);
          else if (type.equals("quality"))
            fill(255, 255, 255);
          if (v.isHanging())
            fill(180, 180, 220);
          //if (v.isActive()) 
          //  fill(220, 180, 180);
          
          strokeWeight(1.5f);
          ellipse(x, y, getNodeWidth(v), getNodeHeight(v));

          stroke(0);
          fill(25, 25, 120);

          // 2.3 Print the node names
          //textFont(qi_font,scaleZ(12));
          textAlign(CENTER,CENTER);
          if (v.isHanging())
            fill(255, 255, 255);
          if (qi_physics.numberOfParticles() > qi_maxNrParticles
              && !v.isHovering())
            drawText(v.getAbbreviation(), x,y);
          else {
            if (textWidth(v.getName()) < getNodeWidth(v) || v.getName().equals("Start!"))
              drawText(v.getName(), x,y);
            else {
              if (v.getName().equals("Implementation"))
                drawText("Implemen-\ntation", x,y);
              else {
                drawText(v.getName().replace(" ", "\n"), x,y);
              }
            }
          }
        }
      }
      
     // 2.0 draw children, selects, constraints
	    for (QNode v : qi_physics.getQNodes()) {
	      // 2.1 Assign colors to different parts of the graph
	      // if (i == 0)
	      // fill(200);
	      // else
	      fill(100);

	      // 2.2 Draw ellipses for the nodes (and double ellipses for
	      // those
	      // with hidden parents)
	      float x = v.getNodePosition().x;
	      float y = v.getNodePosition().y;

	      // 2.4 Print labels for the children of each node
	      if (qi_help && !(qi_hideRuns && v.getType().equals("run"))) {
	        ArrayList<String> children = qi_physics.previewChildren(v);
	        
	        fill(150, 150, 150);
	        ////textFont(qi_font);
	        textAlign(LEFT, CENTER);
	        int nr = 1;
	        float offset;
	        for (String s : children) {
	          offset = 0;
	          if (v.getName().length() < 5)
	            offset = textWidth(s + "   ") - getNodeWidth(v);
	          drawSmallText(s.replace("-\n", "").replace("\n", " "), x
	              - getNodeWidth(v) / 2
	              - offset, constraintPosition(v).y + (nr++)
	              * qi_lineHeight);
	        }
	      }

 

	      // 3.0 Draw selects and constraints
	      //textFont(qi_font,int(scaleZ(11)));
	      float padding = qi_lineHeight / 2;

	      // 3.1 Draw selects
	      textAlign(LEFT, CENTER);

              
	      strokeWeight(1f);
	      float off = 2 * padding;
              textFont(qi_font,14);
	      for (String s : v.getAllSelects()) {
	        if (qi_select != null && v == qi_selectedNode && qi_select.equals(s))
	          qi_highLight = true;
                pushMatrix();
                translate(selectPosition(v).x, selectPosition(v).y);
	        rrect(off-padding, -qi_lineHeight+1, textWidth(s) + 2 * padding, qi_lineHeight * 1.4f, qi_lineHeight * 0.6f, "green",
	            (v != qi_selectedNode || qi_hideMarkers));
                popMatrix();
	        greenText((v != qi_selectedNode || qi_hideMarkers));
	        drawText(s, selectPosition(v).x + off,
	            selectPosition(v).y - qi_lineHeight * 0.2f);
	        off += textWidth(s) + 3 * padding;
	        qi_highLight = false;
	      }
	      // 3.2 Draw constraints
	      stroke(250, 0, 0, 40);
	      float lineBase = 0;
	      for (String s : v.getAllConstraints()) {
	        if (qi_constraint.getValue() != null && qi_constraint.getValue().equals(s))
	          qi_highLight = true;
	        ArrayList<String> ss = new ArrayList<String>();
	        if (s.length() > 0) {
	          int lines = int( ceil(s.length() / 50f));
	          for (int i = 0; i < lines; i++)
	            ss.add(s.substring(i * 50,
	                min(50 + (i * 50), s.length())));
	          float constraintWidth = 0;
	          for (String cs : ss)
	            constraintWidth = max(constraintWidth,textWidth(cs));
                  pushMatrix();
                  translate(constraintPosition(v).x, constraintPosition(v).y);
	          rrect(padding, - qi_lineHeight
	                  + lineBase, constraintWidth + 2 * padding,
	              qi_lineHeight * (0.4f + lines), padding, "red",
	              (v != qi_selectedNode || qi_hideMarkers));
                  popMatrix();
	          redText((v != qi_selectedNode || qi_hideMarkers));
	          int i = 0;
	          for (String cs : ss)
	            drawText(cs,
	                constraintPosition(v).x + 2 * padding,
	                constraintPosition(v).y + lineBase
	                    + (i++) * qi_lineHeight-3);
	          lineBase += qi_lineHeight * 1.7f + (lines - 1) * qi_lineHeight;
	        }
	        qi_highLight = false;
	      }
	    }



	    // draw select/constraint markers
	    if (!qi_hideMarkers) {
	      // spacing in indicator
	      float d = getSelectSize() / 2.5f;
	      // offsets from center of node
	      float x = qi_selectedNode.getNodePosition().x;
	      float y = qi_selectedNode.getNodePosition().y;
	      float ax = getNodeWidth(qi_selectedNode) * 0.7f;
	      float ay = getNodeHeight(qi_selectedNode) * 0.8f;
	      // radius from center of node
	      float xd = (float) sqrt(ax * ax + ay * ay);
	      // size of mininodes in expand indicator
	      float mininode = getSelectSize() / 5;
	      // centers of indicators (simplifies math)
	      float xc = 0;
	      float yc = 0;

	      strokeWeight(2f);
	      textAlign(LEFT, CENTER);

	      float animationOffset = min(250, millis() - qi_animationStart) / 250f;

	      // Expand indicator
	      // xc = x + ay * animationOffset;
	      // yc = y - ax * animationOffset;
	      if (qi_physics.previewChildren(qi_selectedNode).size() > 0) {
	        xc = x + ax * animationOffset;
	        yc = y - ay * animationOffset;
	        blueGraphics(true);
	        ellipse(xc, yc, getSelectSize(), getSelectSize());
	        blueText(true);
	        pushMatrix();
	        translate(xc, yc);
	        rotate(2 * PI * animationOffset);

                beginShape();
                vertex(-mininode, mininode);
                vertex(0, -mininode);
                endShape();
                beginShape();
                vertex(0, -mininode);
                vertex(mininode,mininode);
                endShape();
                
	        ellipse(0, -mininode, mininode, mininode);
	        ellipse(-mininode, mininode, mininode, mininode);
	        ellipse(mininode, mininode, mininode, mininode);

	        popMatrix();
	        if (qi_help)
	          drawText("expand", xc + getSelectSize(),yc);
	      }

	      // Select indicators
	      xc = x + xd * animationOffset;
	      yc = y;
	      greenGraphics(true);
	      ellipse(xc, yc, getSelectSize(), getSelectSize());
	      pushMatrix();
	      translate(xc, yc);
	      rotate(2 * PI * animationOffset);
	      line(0, d, 0, -d);
	      line(-d, 0, d, 0);
	      popMatrix();
	      greenText(true);
	      if (qi_help)
	        drawText("select property", xc + getSelectSize(),
	            yc);

	      // Constraint indicators
	      xc = x + ax * animationOffset;
	      yc = y + ay * animationOffset;
	      redGraphics(true);
	      ellipse(xc, yc, getSelectSize(), getSelectSize());
	      pushMatrix();
	      translate(xc, yc);
	      rotate(2 * PI * animationOffset);
	      line(-d, 0, d, 0);
	      popMatrix();
	      redText(true);
	      if (qi_help)
	        drawText("add constraint", xc + getSelectSize(),
	            yc);

	      // Collapse indicator
	      xc = x + ay * animationOffset;
	      yc = y + ax * animationOffset;
	      d = 0.85f * d;
	      redGraphics(true);
	      ellipse(xc, yc, getSelectSize(), getSelectSize());
	      pushMatrix();
	      translate(xc, yc);
	      rotate(2 * PI * animationOffset);
	      line(-d, d, d, -d);
	      line(+d, d, -d, -d);
	      popMatrix();
	      redText(true);
	      if (qi_help)
	        drawText("remove", xc + getSelectSize(),yc);
	    }

            if(qi_startVisible || qi_selectVisible || qi_constraintVisible || qi_examplesVisible)
              translate(qi_listWidth/2,0);
              
	    // 4.0 Draw TextBoxes      
            strokeWeight(1f);
	    if (qi_selectVisible || qi_constraintVisible || qi_examplesVisible || qi_startVisible || qi_uploadVisible)
	      drawTextBox();
	  }

    /**
     * Draws the given textbox at the given position
     * 
     * @param tb
     *            The textbox to be drawn
     * @param v
     *            The position where the textbox should be drawn.
     */
    public void drawTextBox() {
      // 1.0 Calculate width of textbox based on the values it contains
      float boxLineHeight = getListLineHeight();
      textAlign(LEFT, CENTER);
      noSmooth();

      // 3.0 Hovering
      float xx = (mouseX - width / 2);
      float yy = (mouseY - height / 2);
      if (!mousePressed)
        scroll(xx, yy);

      // 4.0 Draw entries of the list
      int os = 0;
      if (qi_constraintEdited || qi_uploadVisible)
        os = 2;
      
      pushMatrix(); 
      translate(scaleX((width-334)), scaleY(0));        
      
      // 2.0 Draw header
      if (qi_constraintEdited || qi_uploadVisible) {
        stroke(255, 0, 0);
        fill(255, 80, 80);
      } else {
        stroke(0, 155, 0);
        fill(80, 155, 80);
      }
      rect(0, 0, qi_listWidth, boxLineHeight);
      fill(0xFFFFFFFF);
      if(qi_uploadVisible)
        drawText("Add short description:", qi_paddingLeft, boxLineHeight / 2);
      else
        drawText(getHeader(), qi_paddingLeft, boxLineHeight / 2);
      
      if (qi_selectEdited) {
          // done button
          drawTextBoxButton(qi_listWidth - 50, 3, "done");
          drawTextBoxButton(qi_listWidth - 110, 3, "delete");
        }
      else if (qi_constraintEdited) {
          // done button
          drawTextBoxButton(qi_listWidth - 50, 3, "done");
          drawTextBoxButton(qi_listWidth - 110, 3, "delete");

          translate(0, boxLineHeight);
          // constraint
          fill(0xFFFFFFFF);
          stroke(150, 0, 0);
          rect(0, 0, qi_listWidth, boxLineHeight);

          //constraint now printed through javascript text field
          //fill(20, 20, 20);
          //drawText(qi_constraint.getValue(), qi_paddingLeft, boxLineHeight * 0.5f);
          //stroke(0, 0, 0);
          //float cursorX = qi_paddingLeft
          //    + textWidth(qi_constraint.getValue().substring(0, qi_constraintCursor));
          //float cursorY = boxLineHeight * 0.25f;
          //if (frameCount / 10 % 2 == 0)
          //  line(cursorX, cursorY, cursorX, cursorY + qi_lineHeight * 1.5f);

          // second header
          translate(0, boxLineHeight);
          stroke(0, 155, 0);
          fill(80, 155, 80);
          rect(0, 0, qi_listWidth, boxLineHeight);
          fill(0xFFFFFFFF);
          drawText(getSecondHeader(), qi_paddingLeft, boxLineHeight * 0.5f);
        }
     if(qi_uploadVisible){
       drawTextBoxButton(qi_listWidth - 50, 5, "save");
       translate(0, boxLineHeight);
       // constraint
       fill(0xFFFFFFFF);
       stroke(150, 0, 0);
       rect(0, 0, qi_listWidth, boxLineHeight);
     }
        
      //calculate offset based on typed keys
      if (qi_constraintVisible) {
        String newSearch = "";
        if(qi_constraint!=null && qi_constraint.getValue().contains(" = "))
           newSearch = qi_constraint.getValue().toLowerCase().split(" = ")[1];
        if(!newSearch.equals(qi_searchString) && (!newSearch.equals("value"))){
              qi_searchString = newSearch;
              for (int i = 0; i < qi_selectionList.size(); i++)
                if (qi_selectionList.get(i).toLowerCase()
                    .startsWith(qi_searchString)) {
                  qi_scrollOffset = min(i, qi_selectionList.size()
                      - qi_listLength + 3);
                  if(i<qi_selectionList.size() - qi_listLength + 3)
                    qi_scrollOffset--;
                }
        }
        }
      if(!qi_uploadVisible){
      for (int i = 0; i < min(getSelectionLength(), qi_listLength - os); i++) {
        stroke(0, 155, 0);
        fill(20, 20, 20);
        String item = getListLabel(i);
        //println(item);
        if (item != null) {
          // 4.0.1 Hide any aliasing information present in items 
          if(item.contains(".") && item.indexOf(".")<3 && getHeader()!=null && getHeader().endsWith("(required)"))
            item = item.substring(item.indexOf(".")+1);

        translate(0, boxLineHeight);
          // 4.1 Draw selected entry (under the mouse pointer)
          if (getItemNumber(xx, yy - qi_listOffset) == i) {
            greenText(true);
            rect(0, 0, qi_listWidth, boxLineHeight);
            whiteText(true);
          }
          // 4.2 Draw other entries
          else {
            fill(0xFFFFFFFF);
            stroke(0, 155, 0);
            rect(0, 0, qi_listWidth, boxLineHeight);
            grayText(true);
          }
          if (textWidth(item) < qi_listWidth - qi_paddingLeft) {
            drawText(item, qi_paddingLeft, boxLineHeight * 0.5f);
          } else {
            textFont(qi_font,12);
            String[] words = item.split(" ");
            String mytext = words[0];
            int c = 0;
            while (c < words.length - 1
                && textWidth(mytext + " " + words[++c]) < qi_listWidth - 10)
              mytext += " " + words[c];
            if(item.equals(mytext))
              drawSmallText(mytext, qi_paddingLeft, boxLineHeight * 0.5f);
            else
              drawSmallText(mytext, qi_paddingLeft, boxLineHeight * 0.3f);
            if (words.length - 1 > c) {
              mytext = words[c];
              while (c < words.length - 1
                  && textWidth(mytext + " " + words[++c]) < qi_listWidth - 10)
                mytext += " " + words[c];
              if (textWidth(mytext + " " + words[c]) >= qi_listWidth - 10)
                mytext += "...";
              drawSmallText(mytext, qi_paddingLeft, boxLineHeight * 0.7f);
            }
          }
        }
      }      

      // 4.2.1 Fill out list with empty items
      for (int i = getSelectionLength() + os; i < qi_listLength; i++) {        
        translate(0, boxLineHeight);
        fill(0xFFFFFFFF);
          stroke(0, 155, 0);
        rect(0, 0, qi_listWidth, boxLineHeight);
      }
      }
      popMatrix();
      
      if(qi_waiting){
        fill(20, 20, 20);
        drawText("Loading...", scaleX((width-334))+qi_paddingLeft, boxLineHeight * 0.5f);
      }
    }
    
    public void drawTextBoxButton(float x, float y, String mytext) {
      smooth();
      //textFont(qi_font,int(scaleZ(12)));
      if (qi_constraintVisible || qi_uploadVisible)
        stroke(200, 40, 40);
      else if (qi_selectVisible)
        stroke(0, 80, 0);
      pushMatrix();
      translate(x,y);
      rrect(-1, -1, textWidth(mytext)*1.2f+2, 1.7*qi_lineHeight+2,
          qi_lineHeight/2, "", true);
      if (qi_constraintVisible || qi_uploadVisible) {
        stroke(150, 0, 0);
        fill(200, 40, 40);
        
      } else if (qi_selectVisible) {
        stroke(0, 120, 0);
        fill(40, 120, 40);
      }
      rrect(0, 0, textWidth(mytext)*1.2f, 1.7*qi_lineHeight,
          (qi_lineHeight-4) / 2, "", true);
      popMatrix();        
      textAlign(CENTER);  
      fill(255,255,255);
      drawSmallText(mytext, x+textWidth(mytext)*0.6f, y + qi_lineHeight*1.2f);
      textAlign(LEFT, CENTER);
      noSmooth();
    }
	  
	  public void drawWarning() {
	    if (qi_warning != null) {
	      textAlign(CENTER, CENTER);
	      noSmooth();
	      fill(255, 75, 75);
              int x=0;
              if(qi_constraintVisible || qi_selectVisible || qi_startVisible)
                x=-qi_listWidth/2;
	      drawText(qi_warning, x , scaleY(10));
	    }
	  }

	  public PVector getCorrectedMaxPosition(QNode q) {

	    float maxc = 0;
	    float maxv = 0;
	    float delta = constraintPosition(q).x - q.getNodePosition().x;
	    for (String s : q.getConstraints()) {
	      ArrayList<String> ss = new ArrayList<String>();
	      if (s.length() > 0) {
	        int lines = int( ceil(s.length() / 50f));
	        float constraintWidth = 0;
	        for (int i = 0; i < lines; i++)
	          constraintWidth = max(
	              constraintWidth,
	              textWidth(s.substring(i * 50,
	                  min(49 + (i * 50), s.length()))));
	        maxc = max(constraintWidth, maxc);
	        maxv += getLineHeight() * (lines + 0.5f);
	      }
	    }
	    for (String s : q.getSelects())
	      maxc = max(maxc, textWidth(s));
	    if (maxv > height / 2)
	      maxv = height / 2;
	    PVector vector = new PVector();
	    vector.x = q.getNodePosition().x + delta + maxc;
	    vector.y = q.getNodePosition().y + getNodeHeight(q) + maxv;
	    return vector;
	  }

	  public String getHeader() {
	    return qi_boxHeader;
	  }

	  /**
	   * Returns the number of the visible row selected by the given x and y
	   * coordinates. Value between 0 and length.
	   * 
	   * @param x
	   * @param y
	   * @param listX
	   * @param listY
	   * @param lineheight
	   * @return
	   */

	  public int getItemNumber(float x, float y) {
	    if (x >= getListPosition()) {
	      int relpos = int( -ceil((scaleY(0) + getListLineHeight() - y)
	          / getListLineHeight()));
	      if (qi_constraintEdited)
	        relpos -= 2;
	      return relpos;
	    }
	    return -1;
	  }

	  /**
	   * Returns the current text line height
	   * 
	   * @return The current text line height
	   */
	  public float getLineHeight() {
	    return qi_lineHeight;
	  }

	  public String getListLabel(int i) {
	    int l = qi_listLength - 1;
	    if (qi_constraintEdited)
	      l -= 2;
	    if (i == 0 && qi_scrollOffset > 0 && !mousePressed)
	      return "More...";
	    else if (i == l - 1 && qi_scrollOffset < (qi_selectionList.size() - l)
	        && qi_selectionList.size() > l && !mousePressed)
	      return "More...";
	    else if (i + qi_scrollOffset >= 0
	        && i + qi_scrollOffset < qi_selectionList.size())
	      return qi_selectionList.get(i + qi_scrollOffset);
	    else
	      return null;
	  }

	  public float getListLineHeight() {
	    return float(height-1)/qi_listLength;
	    // return abs(scaleY((int)lineHeight))/(listLength/2);
	  }

	  public float getListPosition() {
	    return scaleX((width-334));
	  }

	  public float getListWidth() {
	    return abs(scaleX(qi_listWidth));
	  }

	  public float getNodeHeight(QNode q) {
	    return getNodeWidth(q) / 2;
	  }

	  /**
	   * Returns the list of nodes which forms the intersection of the two given
	   * lists of nodes
	   * 
	   * @param someNodes
	   *            The first list of nodes
	   * @param someOtherNodes
	   *            The second list of nodes
	   * @return The intersection of the two given node lists
	   */
	  private ArrayList<QNode> getNodeIntersection(ArrayList<QNode> someNodes,
	      ArrayList<QNode> someOtherNodes) {
	    ArrayList<QNode> known = new ArrayList<QNode>();
	    for (QNode c : someNodes)
	      for (QNode n : someOtherNodes)
	        if (c == n)
	          known.add(c);
	    return known;
	  }

	  public float getNodeWidth(QNode q) {
	    float qtextwidth = 0;
	    if (qi_physics.numberOfParticles() > qi_maxNrParticles && !q.isHovering())
	      // textwidth = textWidth(q.getAbbreviation() + "ww");
	      qtextwidth = 20;
	    else
	      // for(String s: q.getName().split("\n"))
	      // textwidth = Math.max(textWidth(s + "ww"),textwidth);
	      qtextwidth = 90;

	    // textwidth = Math.max(textwidth,80);
	    return qtextwidth;
	  }

	  /**
	   * Returns the query represented by the query graph.
	   * 
	   * @return The SQL query represented by the composed query graph
	   *
	  public String getQuery() {
	    return buildQuery();
	  }*/

	  public ArrayList<ArrayList<String>> getRows() {
	    return qi_rows;
	  }

	  public String getSecondHeader() {
	    return qi_secondBoxHeader;
	  }

	  public String getSelection(float x, float y) {
	    if (getItemNumber(x, y) >= 0)
	      return getListLabel(getItemNumber(x, y));
	    return null;
	  }

	  public int getSelectionLength() {
	    return min(qi_listLength, qi_selectionList.size());
	  }

	  public float getSelectSize() {
	    return qi_selectSize;
	  }

	  /**
	   * Returns the position of the TextBox with the example queries.
	   * 
	   * @return The coordinates of the TextBox with the example queries.
	   */
	  public PVector getTextBoxPosition() {
	    return new PVector(scaleX(10), scaleY(10), 0);
	  }

	  /**
	   * Returns the current warning message.
	   * 
	   * @return The current warning message string
	   */
	  public String getWarning() {
	    return qi_warning;
	  }

	  public void grayGraphics(boolean foreground) {
	    stroke(40, 40, 40, 200);
	    fill(200, 200, 200);
	  }

	  public void grayText(boolean foreground) {
	    fill(40, 40, 40);
	  }

	  public void greenGraphics(boolean foreground) {
	    if (foreground) {
	      stroke(40, 155, 40, 120);
	      fill(200, 255, 200);
	    } else {
	      stroke(0, 155, 0, 20);
	      fill(0, 155, 0, 20);
	    }
	    if (qi_highLight)
	      fill(80, 155, 80);
	  }

	  public void greenText(boolean foreground) {
	    if (foreground)
	      fill(80, 155, 80);
	    else
	      fill(0, 155, 0, 40);
	    if (qi_highLight)
	      fill(200, 255, 200);
	  }

	  public boolean isTouched(QNode q, float x, float y) {
	    PVector p = q.getNodePosition();
	    return pointWithinEllipse(p.x, p.y, getNodeWidth(q) / 2,
	        getNodeHeight(q) / 2, x, y);
	  }

	  /**
	   * Handles keystrokes while constraint TextField is visible. Updates the
	   * currently edited constraint. The return key closes the TextField.
	   */
	  public void keyPressed() {
	  }
	  
	  
	  public void loadFields(QNode q) {
	    //System.out.println("Loading fields");
	    if (q.getFields() == null || q.getFields().size()==0){
              q.setFields(new ArrayList<String>());
              $.getJSON(expdburl+"api_query/?q=describe%20"+q.getFullName().toLowerCase()+"&id="+q.getAlias(),readFields);
            }
            for (QNode c : q.getChildren())
              loadFields(c);
          }
  
          function readFields(var data) {
             QNode q;
             for(QNode n : qi_physics.getQNodes())
               if(n.getAlias().equals(data.id))
                 q=n;
             if(q!=null){
             for (int i = 0, end = data.data.length; i<end; i++) {
               var s = data.data[i][0];
                if (!s.equals("sid")
                  && !s.equals("rid")
                  && !s.equals("did")
                  && !s.equals("parent")
                  && !s.equals("setup")
                  && !s.equals("run")
                  && !s.equals("data")
                  && !s.equals("source")
                  && !s.equals("inputData")
                  && !s.equals("learner")
                  && !s.equals("input")
                  && !s.equals("CVSetup")
                  && !(qi_constraintVisible
                      && q.getFullName().equals("Evaluation") && (s
                      .equals("value") || s.equals("stdev") || s.equals("label")))
                  && !(q.getFullName().endsWith("Quality") && (s
                      .equals("implementation") || s.equals("data") || s
                      .equals("quality"))))
                  q.addField(s);
            }
            }
          }
          
	  public void loadList(String query) {
	    qi_selectionList = new ArrayList<String>();
	    qi_scrollOffset = 0;
	    qi_searchString = "";
	    if (query.startsWith("select"))
	      query += " limit 0,1000";
	    var query2 = encodeURI(expdburl+"api_query/?q="+query, "UTF-8");
            qi_waiting = true;
            $.getJSON(query2,readValues);
	  }

           function readValues(var data) {
             qi_waiting = false;
             for (int i = 0, end = data.data.length; i<end; i++) {
               String l = data.data[i][0];
               if (l!=null && l.length()>0 && !l.contains(":"))
                  qi_selectionList.add(l);
             }
           }
           
           function readQueryGraph(var data) {
             String s = data.data[0][0];
             qi_physics.buildGraph(s);
           }
           
           public void loadExample(String sel){
             var query = encodeURI(expdburl+"api_query/?q=select query from query_graphs where description='" + sel + "'&id=", "UTF-8");
             $.getJSON(query,readQueryGraph);
           }    
    
   /** public void runQuery(String theQuery) {
        if (theQuery.length() == 0)
          return;
        //mixing javascript
        var query = encodeURI(expdburl+"expdbviz.php?q="+theQuery+"&id=", "UTF-8");
        println(query);
        qi_waiting = true;
        var response = $.getJSON(query,readRows);
    }
    
          function readRows(var data) {
             qi_waiting = false;
             println(data.status);
             
             int nrRows = data.data.length;
             int nrCols = data.columns.length;
             String l = "";
             for (int i = 0; i<nrCols; i++) {
               l += data.columns[i].title+"\t";             
             }             
             l = "";  
             for (int i = 0; i<nrRows; i++) {
               for (int j = 0; j<nrCols; j++)
                 l += data.data[i][j]+"\t";
               l += "\n";
             }
           }**/

	  /**
	   * Handles mouse clicks.
	   */
	  public void mouseClicked() {
            loop();
	    float xx = (mouseX - width / 2);
	    float yy = (mouseY - height / 2);
	    QNode q;

	    // ///////////////////////////////////////////// 1. Button clicked
	    String button = buttonClicked(xx, yy);
	    if (button != null) {
	      if (button.equals("upload")){
                if(!qi_initialized)
                  setWarning("Nothing to save.");
                else{
                qi_uploadVisible = true;
                qi_constraint.setValue("");
                qi_constraint.show();}
	      } else if (button.equals("save")) {
                writeXML(qi_constraint.getValue());
                qi_constraint.reset();
                qi_constraint.hide();
                qi_uploadVisible = false;
              } 
	      else if (button.equals("examples")) {
	        pushSelection("examples");
	        qi_examplesVisible = true;
	      } else if (button.equals("download")){
                pushSelection("download");
                qi_examplesVisible = true;
              } else if (button.equals("restart")) {
	        qi_initialized = false;
	        restart();
	      } else if (button.equals("help")) {
	        qi_help = !qi_help;
	        qi_helpAnimationStart = millis();
	      } else if (button.equals("done")
	          && !getHeader().contains("(required)")) {
	        if (qi_constraintEdited) {
	          pushConstraint();
	        } else if (qi_selectEdited) {
	          resetSelect();
	        }
	      } else if (button.equals("delete")) {
	        if (qi_constraintEdited) {
	          qi_selectedNode.removeConstraint(qi_constraint.getValue());
	          resetConstraint();
	        } else if (qi_selectEdited) {
	          qi_selectedNode.removeSelection(qi_select);
	          resetSelect();
	        }
	      }
	      qi_drag=0;
	    }
	    // ///////////////////////////////////////////// 2. Example query
	    // selected
	    else if (qi_examplesVisible) {
	      String sel = getSelection(xx, yy);
	      if (sel != null) {
	        loadExample(sel);
	      }
	      qi_examplesVisible = false;
	      // 2.2 Handle clicked aggregates
	    }
	    // ///////////////////////////////////////////// 3. Start procedure
	    // selected
	    else if (qi_physics.getTopNodes().size() > 0
	        && qi_physics.getTopNodes().get(0).getName().equals("Start!")
	        && isTouched(qi_physics.getTopNodes().get(0), xx, yy)) {
	      qi_hideMarkers = true;
	      pushSelection("start");
	      qi_startVisible = true;
	    } else if (!qi_initialized) {
	      //System.out.println("Not initialized");
	      String sel = getSelection(xx, yy);
	      if (sel != null) {
	        openTopNode(sel);
	        loadFields(qi_physics.getTopNodes().get(0));
	        qi_initialized = true;
	        qi_startVisible = false;
	      }
	    }
	    // ///////////////////////////////////////////// 4. Item from selection
	    // list clicked
	    else if (qi_selectVisible && boxTouched(xx, getListPosition())) {
	      String selection = getSelection(xx, yy);
	      if (selection != null) {
	        // ///////////////////////////////////////////// 4.1 New
	        // selection field selected
	        if (qi_select == null) {
	          qi_selectedNode.addSelect(selection);
	          qi_select = selection;
	          pushSelection("functions");
	        }
	        // ///////////////////////////////////////////// 4.2 Existing
	        // selection field edited
	        else {
	          boolean close = true;
	          String oldselect = qi_select;
	          selection = selection.split(" \\(")[0];
	          if (selection.equals("list all values"))
	            selection = "group_concat";
	          if (selection.equals("count distinct"))
	            qi_select = "count (distinct " + qi_select + ")";
	          else if (selection.equals("distinct"))
	            qi_select = "distinct " + qi_select;
	          else if (selection.equals("crosstabulate")) {
	            pushSelection("crosstabulate");
	            close = false;
	          } else if (getHeader().startsWith("Add 1 column")) {
	            qi_select = qi_select + " per " + selection;
	            pushSelection("Group by:crosstab");
	            close = false;
	          } else if (getHeader().startsWith("Show")) {
	            if (!qi_select.contains(" over "))
	              qi_select = qi_select + " over " + selection;
	            else
	              qi_select = qi_select + "," + selection;
	            pushSelection("ctfunctions");
	            close = false;
	          } else if (getHeader().startsWith("Take")) {
	            if (!qi_select.contains(" over "))
	              qi_select = qi_select + " over " + selection;
	            else
	              qi_select = qi_select + "," + selection;
	          } else if (getHeader().startsWith("Combine")) {
	            if (qi_select.contains(" over "))
	              qi_select = selection + "("
	                  + qi_select.split(" over ")[0] + ") over "
	                  + qi_select.split(" over ")[1];
	            else
	              qi_select = selection + "(" + qi_select + ")";
	          } else {
	            qi_select = selection + "(" + qi_select + ")";
	            pushSelection("Group by:" + selection);
	            close = false;
	          }
	          qi_selectedNode.replaceSelect(oldselect, qi_select);
	          if (close)
	            resetSelect();
	        }
	      }
	      // ///////////////////////////////////////////// 4. Item from
	      // constraint list clicked
	    } else if (qi_constraintVisible && boxTouched(xx, getListPosition())) {
	      int itemnr = getItemNumber(xx, yy);      
	        boolean done = false;
	        if (itemnr >= 0 && pushSelectionToConstraint(getSelection(xx, yy))) {
	          pushConstraint();
	        
	      }
	      // ///////////////////////////////////////////// 5. Node marker
	      // clicked
	    } else if (!qi_hideMarkers && checkMarkers(xx, yy) != null) {
	      if (checkMarkers(xx, yy).equals("select")) {
	        qi_selectVisible = true;
	        pushSelection("select");
	        qi_hideMarkers = true;
	      } else if (checkMarkers(xx, yy).equals("constraint")) {
	        qi_constraintVisible = true;
                qi_constraint.show();
	        pushSelection("constraint");
	        qi_hideMarkers = true;
	      } else if (checkMarkers(xx, yy).equals("expand")) {
	        qi_physics.expand(qi_selectedNode);
	        loadFields(qi_selectedNode);
	        qi_hideMarkers = true;
	      } else if (checkMarkers(xx, yy).equals("collapse")) {
	        qi_physics.removeQNode(qi_selectedNode);
	        if (qi_hideRuns && qi_selectedNode.getType().equals("setup") && qi_selectedNode.getName().equals(qi_selectedNode.getParent().getName())
	            && qi_selectedNode.getParent().getType().equals("run"))
	          qi_physics.removeQNode(qi_selectedNode.getParent());
	        qi_hideMarkers = true;
	      } else {
	        //System.out.println("Unknown marker selected!");
	      }
	      // ///////////////////////////////////////////// 6. Expand node by
	      // double-click
	    } else if (!qi_hideMarkers && qi_previousSelectedNode==qi_selectedNode && isTouched(qi_selectedNode, xx, yy)) {
	      qi_physics.expand(qi_selectedNode);
	      loadFields(qi_selectedNode);
	      qi_hideMarkers = true;
	    }
	    // ///////////////////////////////////////////// 7. Node events
	    else if ((q = touchedNode(xx, yy)) != null) {
	      // ///////////////////////////////////////////// 7.1 Node clicked
	      if (isTouched(q, xx, yy)) {
	        if (q.isHanging() && q.getParent() != null) {
	          q.setHanging(false);
	                  
	          //choosing a run node also opens its children
	          if (q.getType().equals("run")) {
	            qi_physics.expand(q);
	            loadFields(qi_selectedNode);
	          }
	        } else {// show markers
	          qi_hideMarkers = false;
	          qi_animationStart = millis();
	          resetSelect();
	          resetConstraint();
	        }
	        //remove all remaining hanging nodes
	        qi_physics.removeHangingNodes();
	      }
	      // ///////////////////////////////////////////// 7.2 Node selection
	      // clicked
	      else if (selectTouched(q, xx, yy) != null && q!=null) {
	        qi_selectedNode = q;
	        qi_select = selectTouched(q, xx, yy);
	        if (qi_select.contains("("))
	          pushSelection("Group by:" + qi_select.split("\\(")[0]);
	        else
	          pushSelection("functions");
	        qi_selectEdited = true;
	        qi_selectVisible = true;
	      }
	      // ///////////////////////////////////////////// 7.3 Node constraint
	      // clicked
	      else if (constraintTouched(q, xx, yy) != null) {
	        qi_selectedNode = q;
	        qi_previousConstraint = constraintTouched(q, xx, yy);
	        pushSelection("constraint:" + qi_previousConstraint);
	        qi_constraintVisible = true;
                qi_constraint.show();
	      }
	    }
	    // ///////////////////////////////////////////// 8 Required step
	    else if ((qi_selectVisible || qi_constraintVisible || qi_examplesVisible)
	        && getHeader().contains("(required)")) {
	      // do nothing
	    }
	    // ///////////////////////////////////////////// 9 Background clicked
	    else {
	      // hide markers
	      qi_hideMarkers = true;
	      // hide textboxes
	      resetConstraint();
	      resetSelect();
              qi_uploadVisible=false;
              qi_constraint.reset();

	      // hide hanging nodes
	      qi_physics.removeHangingNodes();
	      setWarning("");
	    }
            qi_drag=0;
	    if(qi_selectedNode!=null)
	      qi_previousSelectedNode = qi_selectedNode;
	  }

	  /**
	   * Handle mouse drags. Dragged nodes are moved and query graph is allowed to
	   * realign.
	   */
	  public void mouseDragged() {
            loop();
	    float xx = (mouseX - width / 2);
	    float yy = (mouseY - height / 2);
	    if (qi_selectedNode == null) {
	      boolean done = false;
	      int i = 0;
	      while (!done && i < qi_physics.numberOfParticles()) {
	        QNode q = qi_physics.getQNodes().get(i++);
	        if (isTouched(q, xx, yy)) {
	          qi_selectedNode = q;
	          done = true;
	        }

	      }
	    } else if (!qi_selectVisible && !qi_constraintVisible && !qi_examplesVisible) {
	      qi_selectedNode.setNodePosition(xx, yy, qi_selectedNode.getNodePosition().z);
	    } else if (qi_selectVisible || qi_constraintVisible || qi_examplesVisible) {
	      qi_listOffset = (yy - qi_oldY);

	      int move = ceil(qi_listOffset / getListLineHeight());
	      if (qi_listOffset > 0)
	        move = floor(qi_listOffset / getListLineHeight());
	      while (abs(move) != 0) {
	        if (move < 0) {
	          if (qi_scrollOffset <= (qi_selectionList.size() - (qi_listLength - 2))) {
	            qi_scrollOffset++;
	            qi_listOffset += getListLineHeight();
	            qi_oldY = yy;
	          }
	          move++;
	        } else {
	          if (qi_scrollOffset > 0) {
	            qi_scrollOffset--;
	            qi_listOffset -= getListLineHeight();
	            qi_oldY = yy;
	          }
	          move--;
	        }
	      }
	    }
            qi_drag += 10;
	  }

	  /**
	   * Handle mouse moves. When mouse hovers over nodes and nodes are shrunk,
	   * enlarge nodes to show full name. Only for large query graphs.
	   */
	  public void mouseMoved() {
            loop();
	    if (qi_physics.numberOfParticles() > qi_maxNrParticles) {
	      for (QNode q : qi_physics.getQNodes()) {
	        float xx = (mouseX - width / 2);
	        float yy = (mouseY - height / 2);
	        if (isTouched(q, xx, yy) || checkMarkers(xx, yy) != null)
	          q.setHovering(true);
	        else
	          q.setHovering(false);
	      }
	    }
	    int i = 0;
	    boolean done = false;
	  }

	  /**
	   * While mouse is pressed, allow nodes to be dragged.
	   */
	  public void mousePressed() {
            loop();
	    float xx = (mouseX - width / 2);
	    float yy = (mouseY - height / 2);
	    qi_oldX = xx;
	    qi_oldY = yy;
	    // setMessage(xx + " \t" + yy);

	    boolean done = false;
	    int i = 0;
	    while (!done && i < qi_physics.getQNodes().size()) {
	      QNode q = qi_physics.getQNodes().get(i++);
	      if (isTouched(q, xx, yy)) {
	        if (q != qi_previousSelectedNode
	            && !(qi_hideRuns && q.getType().equals("run"))) {
	          qi_selectedNode = q;
	          done = true;
	        } else if (q == qi_previousSelectedNode){
	          //hideMarkers = !hideMarkers;
	        }
	      }
	    }
	    qi_dragging = true;
	  }

	  /**
	   * While mouse is released, don't allow nodes to be dragged.
	   */
	  public void mouseReleased() {
	    qi_dragging = false;
	    if (round(qi_listOffset) > 0 && qi_scrollOffset > 0)
	      qi_scrollOffset--;
	    if (round(qi_listOffset) < 0
	        && qi_scrollOffset <= (qi_selectionList.size() - (qi_listLength - 2)))
	      qi_scrollOffset++;
	    qi_listOffset = 0;
	  }

	  public void openTopNode(String s) {
	    QNode q = null;
	    if (s.equals("Cross-validation runs"))
	      q = qi_physics.makeTopQNode("Cross Validation", "CVRun", "cvr", "run");
	    else if (s.equals("Datasets"))
	      q = qi_physics.makeTopQNode("Dataset", "Dataset", "d", "data");
	    else if (s.equals("Algorithms"))
	      q = qi_physics.makeTopQNode("Implementation", "Implementation", "i",
	          "setup");
	    else if (s.equals("Mathematical functions"))
	      q = qi_physics.makeTopQNode("Function", "Math_Function", "f", "setup");
	    else if (s.equals("Bias-Variance decomposition runs"))
	      q = qi_physics.makeTopQNode("Bias-Variance Decomposition", "BVRun",
	          "bvr", "run");
	    else if (s.equals("Preprocessing runs"))
	      q = qi_physics.makeTopQNode("Preprocessing", "PPRun", "ppr", "run");
	    else if (s.equals("Experiment setups"))
	      q = qi_physics
	          .makeTopQNode("Experiment", "Experiment", "exp", "setup");
	    if (q != null) {
	      if (qi_physics.getTopNodes().get(0).getName().equals("Start!"))
	        qi_physics.removeTopNode(qi_physics.getTopNodes().get(0));
	      qi_physics.expand(q);
	      for (QNode c : q.getChildren()){
	        c.setHanging(false);
	        if(c.getType().equals("run"))
	          qi_physics.expand(c);
	      }
	    }
	  }

	  private boolean pointWithinEllipse(float posx, float posy, float radx,
	      float rady, float px, float py) {

	    float dx = (px - posx);
	    float dy = (py - posy);

	    return (dx * dx) / (radx * radx) + (dy * dy) / (rady * rady) <= 1;
	  }
	  
	  public void pushConstraint() {
	    if (!qi_constraint.getValue().contains("= Value")) {
	      if (qi_previousConstraint != null)
	        qi_selectedNode.replaceConstraint(qi_previousConstraint, qi_constraint.getValue());
	      else {
	        String precedent = null;
	        String[] cs = qi_constraint.getValue().split(" = ");
	        for (String c : qi_selectedNode.getConstraints())
	          if (cs[0].equals(c.split(" = ")[0]))
	            precedent = c;
	        if (precedent != null) {
	          qi_selectedNode.replaceConstraint(precedent, precedent
	              + " or " + qi_constraint.getValue());
	        } else{
                   if(qi_selectedNode==null)
                     println("Selected node is null!");
                   else
	             qi_selectedNode.addConstraint(qi_constraint.getValue());
                }
	      }
	    }
	    resetConstraint();
	  }
	  
	  public void pushSelection(String type) {
	    if (type.equals("examples")) {
	      qi_boxHeader = "Choose example";
	      qi_query = "select description from query_graphs where favorite='1' order by graphID";
	    } else if (type.equals("download")) {
              qi_boxHeader = "Download graph";
              qi_query = "select description from query_graphs where favorite='0' order by graphID";
            } else if (type.equals("select")) {
	      qi_boxHeader = "Select property";
	      doPropertyQuery();
	      qi_selectEdited = true;
	    } else if (type.equals("constraint")) {
	      qi_boxHeader = "Compose constraint";
	      qi_secondBoxHeader = "Select property";
	      doPropertyQuery();
	      qi_constraintEdited = true;
	    } else if (type.startsWith("constraint:")) {
	      qi_boxHeader = "Compose constraint";
	      qi_secondBoxHeader = "Select property";
	      qi_constraintEdited = true;
	      qi_constraint.setValue(type.split(":")[1]);
	      doPropertyQuery();
	    } else if (type.equals("start")) {
	      qi_boxHeader = "Show me...";
	      qi_selectionList = new ArrayList<String>();
	      qi_selectionList.add("Cross-validation runs");
	      qi_selectionList.add("Datasets");
	      qi_selectionList.add("Algorithms");
	      qi_selectionList.add("Mathematical functions");
	      qi_selectionList.add("Bias-Variance decomposition runs");
	      qi_selectionList.add("Preprocessing runs");
	      qi_selectionList.add("Experiment setups");
	    } else if (type.equals("functions")) {
	      qi_boxHeader = "Add function?";
	      qi_selectionList = new ArrayList<String>();
	      if (qi_select.contains("value")) {
	        qi_selectionList.add("max");
	        qi_selectionList.add("min");
	        qi_selectionList.add("avg");
	        qi_selectionList.add("sum");
	        qi_selectionList.add("std");
	        qi_selectionList.add("variance");
	      }
	      qi_selectionList.add("distinct");
	      qi_selectionList.add("count");
	      qi_selectionList.add("count distinct");
	      qi_selectionList.add("list all values");
	      qi_selectionList.add("crosstabulate (1 column per ...)");
	    } else if (type.equals("ctfunctions")) {
	      qi_boxHeader = "Combine using...";
	      qi_selectionList = new ArrayList<String>();
	      qi_selectionList.add("max");
	      qi_selectionList.add("min");
	      qi_selectionList.add("avg");
	      qi_selectionList.add("sum");
	      qi_selectionList.add("list all values (default)");
	    } else if (type.equals("crosstabulate")) {
	      qi_boxHeader = "Add 1 column per... (required)";
	      qi_selectionList = new ArrayList<String>();
	      //add possible fields to crosstabulate with
	      if(!qi_selectedNode.getName().equals("Evaluation")){
	      for (String s : qi_selectedNode.getFields())
	        qi_selectionList.add(qi_selectedNode.getAlias() + "." + s + " ("
	            + qi_selectedNode.getName() + ")");
	      if(qi_selectedNode.getHiddenChild()!=null){
	        for (String s : qi_selectedNode.getHiddenChild().getFields())
	          qi_selectionList.add(qi_selectedNode.getHiddenChild().getAlias() + "." + s + " ("
	              + qi_selectedNode.getName() + ")");}
	      }
	      for (QNode q : qi_physics.getAllNodes()) {
	        for (String s : q.getSelects())
	          if (!(q == qi_selectedNode && s.equals(qi_select))
	              && !s.contains("(") && !s.contains(" per "))
	            if (!qi_selectionList.contains(q.getAlias() + "." + s
	                + " (" + q.getName() + ")"))
	              qi_selectionList.add(q.getAlias() + "." + s + " ("
	                  + q.getName() + ")");
	        for (String s : q.getConstraints()) {
	          if (s.contains(" or ")) {
	            String c = s.split(" = ")[0];
	            if (!qi_selectionList.contains(q.getAlias() + "." + c
	                + " (" + q.getName() + ")"))
	              qi_selectionList.add(q.getAlias() + "." + c + " ("
	                  + q.getName() + ")");
	          }
	        }
	      }
	      if (qi_selectionList.size() == 0) {
	        hideTextBox();
	        qi_boxHeader = "";
	        setWarning("Please add the field you wish to crosstabulate against first.");
	      }
	    } else if (type.startsWith("Group by:")) {
	      String aggr = type.split("\\:")[1];
	      if (aggr.equals("crosstab"))
	        qi_boxHeader = "Show one row per... (required)";
	      else
	        qi_boxHeader = "Take " + aggr + " over... (required)";
	      qi_selectionList = new ArrayList<String>();
	      for (QNode q : qi_physics.getAllNodes()) {
	        for (String s : q.getSelects())
	          if (q != qi_selectedNode && !s.contains("(")
	              && !s.contains(" per "))// &&
	                          // !select.contains(q.getAlias()+"."+s))
	            qi_selectionList.add(q.getAlias() + "." + s + " ("
	                + q.getName() + ")");
	      }
	      if (qi_selectionList.size() == 0) {
	        hideTextBox();
	        qi_boxHeader = "";
	        setWarning("Nothing to group by. Aggregating over all results.");
	      }
	    }
	    qi_scrollOffset = 0;
	  }

	  /**
	   * Pushes a selected value to the constraint string
	   * 
	   * @param value
	   *            The selected value
	   * @return True if constraint is finished, false otherwise.
	   */
	  public boolean pushSelectionToConstraint(String value) {
	    if (value != null) {
	      if (qi_constraint.getMode().equals("property")) {
	        String[] c = qi_constraint.getValue().split(" = ");
	        if (c.length > 1)
	          qi_constraint.setValue(value + " = " + c[1]);
	        else
	          qi_constraint.setValue(value + " = value");
                //this.qi_constraint = qi_constraint;
	        qi_constraintCursor = qi_constraint.getValue().indexOf("=") + 2;
	        qi_secondBoxHeader = "Select value";
                qi_constraint.highlightValue();
	        doValueQuery(value);
	        return false;
	      } else if (qi_constraint.getMode().equals("value")) {
	        String[] c = qi_constraint.getValue().split(" = ");
	        if (c.length > 1) {
	          if ((keyCode == CONTROL || keyCode == 157)
	              && !c[1].equals("Value"))
	            qi_constraint.setValue(qi_constraint.getValue() + " or " + c[0] + " = " + value);
	          else
	            qi_constraint.setValue(c[0] + " = " + value);
	        }
                //this.qi_constraint = qi_constraint;
	        if (keyCode != CONTROL)
	          return true;
	        return false;
	      } else {
	        //System.out.println("WARNING: Don't know what to do with selection " + value);
	        return true;
	      }
	    }
	    return false;
	  }

	  public void redGraphics(boolean foreground) {
	    if (foreground) {
	      stroke(200, 0, 0, 120);
	      fill(250, 200, 200);
	    } else {
	      stroke(250, 0, 0, 20);
	      fill(250, 0, 0, 20);
	    }
	    if (qi_highLight)
	      fill(250, 0, 0);
	  }

	  public void redText(boolean foreground) {
	    if (foreground)
	      fill(250, 0, 0);
	    else
	      fill(250, 0, 0, 40);
	    if (qi_highLight)
	      fill(250, 200, 200);
	  }

	  public void resetConstraint(){
	    qi_constraint.reset();
	    qi_previousConstraint = null;
	    qi_constraintCursor = 0;

	    hideTextBox();
	    qi_constraintEdited = false;
	  }

	  public void resetSelect() {
	    if (qi_select != null && qi_select.endsWith(" x "))
	      qi_selectedNode.replaceSelect(qi_select,
	          qi_select.substring(0, qi_select.length() - 3));
	    qi_select = null;
	    hideTextBox();
	    qi_selectEdited = false;
	  }

	  public void rrect(float x, float y, float w, float h, float a,
	      String theColor, boolean foreground) {
	    switchColors(theColor, foreground);
	    rect(x, y, w, h, a);
	  }

	  /**
	   * Scales the given X coordinate. Scaling ensures that the query graph fits
	   * in the window.
	   * 
	   * @param x
	   *            The given X coordinate
	   * @return The X coordinate after scaling.
	   */
	  public float scaleX(int x) {
	    return (x - width / 2);
	  }

	  /**
	   * Scales the given Y coordinate. Scaling ensures that the query graph fits
	   * in the window.
	   * 
	   * @param y
	   *            The given Y coordinate
	   * @return The Y coordinate after scaling.
	   */
	  public float scaleY(int y) {
	    return (y - height / 2);
	  }

	  public void scroll(float x, float y) {
	    int item = getItemNumber(x, y);
	    int l = qi_listLength - 1;
	    if (qi_constraintEdited)
	      l -= 2;
	    if (item == 0 && qi_scrollOffset > 0)
	      qi_scrollOffset--;
	    else if (item == l - 1 && qi_selectionList.size() > l
	        && qi_scrollOffset < (qi_selectionList.size() - l))
	      qi_scrollOffset++;
	  }

	  public PVector selectPosition(QNode q) {
	    PVector p = q.getNodePosition();
	    return new PVector(p.x + getNodeWidth(q) / 2, p.y, p.z);
	  }

	  /**
	   * Returns the select value touched by coordinates xx and yy, or null
	   * otherwise.
	   * 
	   * @param xx
	   * @param yy
	   * @param qi_lineHeight
	   * @return
	   */
	  public String selectTouched(QNode q, float xx, float yy) {

	    float x = selectPosition(q).x;
	    float y = selectPosition(q).y - qi_lineHeight * 0.8f;
	    float padding = qi_lineHeight / 2;

	    if (q.getAllSelects().size() == 0 || yy < y
	        || yy > y + qi_lineHeight * 1.2)
	      return null;

	    float offset = x + padding;
	    for (String s : q.getAllSelects()) {
	      if (xx > offset && xx < offset + textWidth(s) + 2 * padding)
	        return s;
	      else
	        offset += textWidth(s) + 3 * padding;
	    }
	    return null;
	  }

	  /**
	   * Sets the current line height
	   * 
	   * @param lineHeight
	   *            The new line height
	   */
	  public void setLineHeight(float lineHeight) {
	    this.qi_lineHeight = lineHeight;
	  }

	  /**
	   * Initializes the applet. Set dimensions, load fonts, start physics engine.
	   */
	  public void setup() {
            //var el = document.getElementById('querygraph');
            //var qi_currwidth=window.getComputedStyle(el,"").getPropertyValue("width").replace("px","");
	    size(1000, 500, P2D);
	    smooth();
	    ellipseMode(CENTER);
            qi_font = createFont("sans-serif",1,true);
            //textFont(qi_font, 32);
            
            qi_constraint = new Constraint();

	    //textMode(MODEL);
	    qi_physics = new QSystem(0, 1f);
	    qi_physics.setIntegrator(ParticleSystem.MODIFIED_EULER);
	    qi_physics.setDrag(1f);
	    
		qi_physics.clear();
		qi_physics.cleanMore();
		qi_physics.makeTopQNode("Start!", "", "", "");
		updateCentroid();
	  }

          public void restart(){
              qi_physics.clear();
              qi_physics.cleanMore();
              qi_physics.makeTopQNode("Start!", "", "", "");
              updateCentroid();
          }

	  /**
	   * Sets the current warning message
	   * 
	   * @param warning
	   *            The new warning message
	   */
	  public void setWarning(String warning) {
	    qi_warning = warning;
            //setQIWarning(warning);
	  }

	  /**
	   * Stops inertia in the physics engine.
	   */
	  public void stopInertia() {
	    for (int i = 0; i < qi_physics.numberOfParticles(); i++) {
	      Particle p = qi_physics.getParticle(i);
	      p.makeFixed();
	    }
	  }

	  public void switchColors(String newColor, boolean fg) {
	    if (newColor.equals("green"))
	      greenGraphics(fg);
	    else if (newColor.equals("red"))
	      redGraphics(fg);
	    else if (newColor.equals("blue"))
	      blueGraphics(fg);
	    else if (newColor.equals("gray"))
	      grayGraphics(fg);
	  }

	  public QNode touchedNode(float xx, float yy) {
	    for (QNode q : qi_physics.getQNodes()) {
	      if (!(qi_hideRuns && q.getType().equals("run") && !q.isHanging())) {
	        if (isTouched(q, xx, yy))
	          return q;
	        if (selectTouched(q, xx, yy) != null)
	          return q;
	        if (constraintTouched(q, xx, yy) != null)
	          return q;
	      }
	    }
	    return null;
	  }

	  /**
	   * Checks whether any node has moved
	   * 
	   * @return True if a node has moved, false otherwise.
	   */
	  public boolean update() {
	    for (QNode q : qi_physics.getQNodes())
	      if (q.hasMoved())
	        return true;
	    return false;
	  }

	  /**
	   * Updates centering and scaling of query graph
	   */
	  public void updateCentroid() {
	    float xMax = -width/2;
	    float xMin = width/2;
            float yMax = -height/2;
	    float yMin = height/2;
            if(qi_physics.getQNodes().size()>1)
	    for (QNode p : qi_physics.getQNodes()) {
	      xMax = max(xMax, getCorrectedMaxPosition(p).x);
	      xMin = min(xMin, p.getNodePosition().x);
	      yMin = min(yMin, p.getNodePosition().y);
	      yMax = max(yMax, getCorrectedMaxPosition(p).y);
	    }
	    float deltaX = xMax - xMin;
            float deltaY = yMax - yMin;
	    if (qi_startVisible || qi_selectVisible || qi_constraintVisible || qi_examplesVisible)
	      deltaX *= (1 + ((float) width - qi_listWidth) / width);
            else
              deltaX *= 1.1f;

            //float f = min(0.82,1-(10-qi_physics.getQNodes().size())/10);
            float f = max(0.65,(deltaX/width));
            qi_scale = min((((float)width)) / (deltaX), (((float)height)) / (deltaY))*f;
            //if(qi_drag<20)
            for(QNode q : qi_physics.getQNodes()){
              if(!q.isHidden()){
              //center nodes
              Particle p = q.getNodeParticle();
              p.position.x += (abs(xMin)-abs(xMax))/2;
              p.position.y += (abs(yMin)-abs(yMax))/2;
                
              //scale distance
              if(q.getParent()!=null){//&& !q.getName().equals(q.getParent().getName())){
                p.position.x *= qi_scale;
                p.position.y *= qi_scale;
              }
              }
            }
            /**for(QNode q : qi_physics.getQNodes()){
              if(q.getParent()!=null){ 
                //q.getParentSpring().setRestLength(q.getNodeParticle().distanceTo(q.getParent().getNodeParticle()));
                println(q.getName()+" "+q.getParent().getName()+" "+q.getParentSpring().restLength());
              }
               else
                 println(q.getNodeParticle().position.x+" "+q.getNodeParticle().position.y);
            }**/
            qi_physics.tick(100);
	  }

	  /**
	   * Rebuilds query
	   */
	  public void updateQuery() {
	      runQuery(buildQuery());
          }

	  public void whiteText(boolean foreground) {
	    // stroke(255, 2, 200, 120);
	    fill(255, 255, 255);
	  }

	  /**
	   * Exports query graph to XML.
	   */
	  public void writeXML(String descr) {
            var newDate = new Date();
            var id = newDate.getTime();
            localStorage["graph"]=qi_physics.getTopNodes().get(0).toString();
            
            //This will automatically download the graph as an XML file
            //var uriContent = "data:application/octet-stream," + encodeURIComponent(localStorage["graph"]);
            //window.open(uriContent, "newQueryGraph");

            $.getJSON(encodeURI(expdburl+"expdbsavegraph.php?descr="+descr+"&graph="+qi_physics.getTopNodes().get(0).toString(),"UTF-8"));
            setWarning("Your graph is uploaded!");
	  }


public class Constraint {
    
    public dat.GUI qi_gui;
    public String c_value;
    public String c_mode="property";
    
    public String getMode(){
      return c_mode;
    }
    
    public void setMode(String m){
       c_mode=m;
    }
    
    public Constraint(){
         var settings = eval("("+ "{autoPlace: false}" + ")");
         qi_gui = new dat.GUI();//settings);
         //println(settings.autoPlace);
         var customContainer = document.getElementById("querygraph").parentNode;
         customContainer.appendChild(qi_gui.domElement);
         this.c_value = "Property = Value";
         qi_gui.add(this, "c_value").listen();
         qi_gui.domElement.style.display = "none";
         qi_gui.domElement.style.position="relative";
         qi_gui.domElement.style.marginTop="-500px";
         qi_gui.domElement.style.marginRight="-90px";
         qi_gui.domElement.style.marginLeft="0px";
         qi_gui.domElement.style.setProperty("width","310px","important");
         qi_gui.__closeButton.style.display = "none";
         //qi_gui.domElement.input.style.background = "#fff";
         var cells = qi_gui.domElement.getElementsByTagName("input"); 
         for (var i = 0; i < cells.length; i++) {
            cells[i].style.backgroundColor="white";
            cells[i].style.fontSize="12px";
            cells[i].style.setProperty("color","black","");
            cells[i].style.setProperty("margin-top","0","");
         }
         var cells = qi_gui.domElement.getElementsByClassName("property-name"); 
         for (var i = 0; i < cells.length; i++) {
            cells[i].style.display = "none";
          }
         var cells = qi_gui.domElement.getElementsByClassName("cr string"); 
         for (var i = 0; i < cells.length; i++) {
            cells[i].style.backgroundColor = "white";
            cells[i].style.border = "none";
            cells[i].style.paddingRight = "0px";
            cells[i].style.setProperty("line-height","23px","");
            cells[i].style.setProperty("height","23px","");
          }
         var cells = qi_gui.domElement.getElementsByClassName("c"); 
         for (var i = 0; i < cells.length; i++) {
            cells[i].style.setProperty("width","100%","");
          }
         var cells = qi_gui.domElement.getElementsByClassName("ul"); 
         for (var i = 0; i < cells.length; i++) {
            cells[i].style.setProperty("height","auto","");
          }
    }
    
    public void show(){
         qi_gui.domElement.style.display = "block";
         qi_gui.domElement.style.marginLeft = "-70px";
         qi_gui.domElement.style.setProperty("width","315px","");
         var pos = c_value.indexOf(" = ");
         if(pos<0)
           pos=8;
         var cells = qi_gui.domElement.getElementsByTagName("input"); 
         for (var i = 0; i < cells.length; i++) {
            cells[i].focus();
            cells[i].setSelectionRange(0,pos);
          }
        c_mode="property";
    }
    
    public void highlightValue(){
          var cells = qi_gui.domElement.getElementsByTagName("input"); 
          for (var i = 0; i < cells.length; i++) {
            cells[i].focus();
            var pos = cells[i].value.indexOf("Value");
            if(pos>0)
                cells[i].setSelectionRange(pos,pos+5);
          }
          c_mode="value";
    }
    
    public void hide(){
         qi_gui.domElement.style.display = "none";
    }
    
    public void setValue(String value){
      c_value=value;
      for (int i=0; i<qi_gui.__controllers.length; i++) {
        qi_gui.__controllers[i].updateDisplay();
      }
    }
    
    public String getValue(){
      return c_value;    
    }
    
    public boolean isComplete() {
      return !c_value.contains("Property") && !c_value.contains("Value");
    }
    
    public void reset(){
     c_value = "Property = Value";
     c_mode = "property";
   }
    
}

/**
 * Class for nodes in a query graph.
 * 
 * @author Joaquin Vanschoren
 */
public class QNode {

	/**
	 * Abbrevated name of this node. Used to shorten the generated query.
	 */
	private String q_abbreviation;

	/**
	 * Activity level of the node. Node is active if it has at least one
	 * constraint or select, or if it connects two nodes who do.
	 */
	private boolean q_active;

	/**
	 * List if this node's children
	 */
	private ArrayList<QNode> q_children;

	/**
	 * Foreign key linking this node to it's children. Used to generate the
	 * inner joins in the SQL query.
	 */
	private String q_connector;

	/**
	 * The list of added constraints for this node
	 */
	private ArrayList<String> q_constraints;

	/**
	 * The depth of the node in the query graph. Used for outputting an XML
	 * description of the node.
	 */
	private int q_depth;

	/**
	 * States whether this node is expanded or not.
	 */
	private boolean q_expanded = false;

	/**
	 * The list of fields for this node
	 */
	private ArrayList<String> q_fields;

	/**
	 * The full name of the node, i.e. the full name of the database table
	 * represented by the node
	 */
	private String q_fullName;

	private boolean q_hidden = false;

	/**
	 * States whether the mouse is hovering over this node
	 */
	private boolean q_hovering = false;

	/**
	 * Whether there can be more of this node under the same parent
	 */
	private boolean q_multipleInstances, q_hanging;

	/**
	 * The multiplicity of the node, or the number of the node in the group of
	 * nodes with the same name.
	 */
	private int q_multiplicity;

	/**
	 * The screen name of the node
	 */
	private String q_name;

	/**
	 * The previous position of the node
	 */
	private PVector q_oldposition;

	/**
	 * Parent node of this node
	 */
	private QNode q_parent;

	/**
	 * Springs that connect this node to its child nodes
	 */
	private Spring q_parentSpring;

	private Particle q_particle;

	private PVector q_position;

	/**
	 * Preferred location of this node between 0 and 360 degrees relevant to the
	 * parent node. -1 means no preferred location
	 */
	private int q_preferredLocation = -1;

	/**
	 * The list of selected attributes for this node
	 */
	private ArrayList<String> q_selects;

	/**
	 * Size of select/constraint markers
	 */
	private float q_selectSize = 24f;

	private String q_type;

	/**
	 * Constructs a new query node
	 * 
	 * @param parent
	 *            This node's parent node. Can be null.
	 * @param name
	 *            The screen name of this node
	 * @param fullName
	 *            The full name of this node
	 * @param abbreviation
	 *            The abbreviated name of this node
	 * @param connector
	 *            The foreign key connecting this node to it's children
	 * @param m
	 *            The weight of this node. Default is 1f
	 * @param s
	 *            The physics engine controlling this node.
	 */
	public QNode(QNode parent, String name, String fullName,
			String abbreviation, String connector, String type, Particle p) {
		q_particle = p;
		if (q_particle == null && parent!=null){
			q_hidden = true;
			q_particle = parent.getNodeParticle();
		}
		this.q_parent = parent;
		this.q_name = name;
		this.q_fullName = fullName;
		this.q_abbreviation = abbreviation;
		this.q_connector = connector;
		this.q_type = type;
		q_children = new ArrayList<QNode>();
		q_selects = new ArrayList<String>();
                q_fields = new ArrayList<String>();
		q_constraints = new ArrayList<String>();
		if (parent != null)
			setDepth(parent.getDepth() + 1);
                q_oldposition = new PVector(getNodePosition().x, getNodePosition().y,getNodePosition().z);
	}

	/**
	 * Adds the given node as this node's child
	 * 
	 * @param child
	 *            The child node
	 */
	public void addChild(QNode child) {
		this.q_children.add(child);
		//System.out.println("Added " + child.getFullName() + " to "
		//		+ getFullName());
	}

	/**
	 * Adds the given constraint to this node's list of constraints. This can
	 * have consequences for other nodes.
	 * 
	 * @param constr
	 *            The constraint to be added
	 */
	public void addConstraint(String constr) {
		// check if constraint belongs to this node
		if ((q_fields == null || getFields().size()==0 || getFields().contains(constr.split(" = ")[0]))
				&& !getConstraints().contains(constr)) {
			// check if constraint exist on same field. If so, add the
			// constraint
			for (String c : getConstraints()) {
				if (c.startsWith(constr.split(" = ")[0])) {
					getConstraints().remove(c);
					getConstraints().add(c += " or " + constr);
					return;
				}
			}
			// if new constraint, simply add it
			getConstraints().add(constr);
			setActive(true);
		} else
			getHiddenChild().addConstraint(constr);
	}

	public void addField(String s) {
		q_fields.add(s);
	}

	/**
	 * Adds the given attribute to the list of selected attributes
	 * 
	 * @param attr
	 *            The selected attribute
	 */
	public void addSelect(String attr) {
		if ((q_fields == null || getFields().size()==0 || getFields().contains(attr))
				&& !getSelects().contains(attr)) {
			getSelects().add(attr);
			setActive(true);
		} else if (getHiddenChild() != null)
			getHiddenChild().addSelect(attr);
	}

	private float distanceSquaredTo(PVector a, PVector b) {
		float dx = a.x - b.x;
		float dy = a.y - b.y;
		float dz = a.z - b.z;
		return dx * dx + dy * dy + dz * dz;
	}

	/**
	 * Return this node's abbreviated name
	 * 
	 * @return This node's abbreviated name
	 */
	public String getAbbreviation() {
		return q_abbreviation;
	}

	public String getAlias() {
		return getAbbreviation() + getMultiplicity();
	}

	public ArrayList<String> getAllConstraints() {
		if (getHiddenChild() == null)
			return getConstraints();
		else {
			ArrayList<String> all = new ArrayList<String>();
			all.addAll(getConstraints());
			all.addAll(getHiddenChild().getConstraints());
			return all;
		}
	}

	public ArrayList<String> getAllSelects() {
		if (getHiddenChild() == null)
			return getSelects();
		else {
			ArrayList<String> all = new ArrayList<String>();
			all.addAll(getSelects());
			all.addAll(getHiddenChild().getSelects());
			return all;
		}
	}

	/**
	 * Returns the name of the oldest ancestor that is not the given top node
	 * (except when this node is the experiment node itself).
	 * 
	 * @return The name of this node's oldest ancestor
	 */
	public String getAncestorUpTo(QNode top) {
		if (getParent() == null)
			return "";
		if (this == top)
			return getName();
		return getParent().getAncestorUpTo(top);
	}
	/**
	 * Retrieves the child with the given name
	 * 
	 * @param q_fullName
	 * @return
	 */
	public QNode getChild(String name) {
		for (QNode q : getChildren())
			if (q.getName().equals(name))
				return q;
		return null;
	}

	public ArrayList<QNode> getChildren() {
		return q_children;
	}

	public String getConnector() {
		return q_connector;
	}

	public ArrayList<String> getConstraints() {
		return q_constraints;
	}

	public int getDepth() {
		return q_depth;
	}

	public ArrayList<String> getFields() {
		return q_fields;
	}

	public String getFullName() {
		return q_fullName;
	}

	public QNode getHiddenChild() {
		for (QNode q : getChildren())
			if (q.isHidden())
				return q;
		return null;
	}

	public String getInstrinsicRole() {
		if (getFullName().equals("AlgorithmSetup")
				&& getName().equals("Cross Validation"))
			return "CrossValidator";
		if (getFullName().equals("AlgorithmSetup")
				&& getName().equals("Learner"))
			return "Learner";
		if (getFullName().equals("AlgorithmSetup")
				&& getName().equals("Data Processing"))
			return "DataPreprocessor";
		return null;
	}

	public String getMultiplicity() {
		if (q_multiplicity == 0)
			return "";
		else
			return q_multiplicity + "";
	}

	public String getName() {
		return q_name;
	}

	public String getOffset() {
		String s = "";
		for (int i = 0; i < getDepth(); i++)
			s += "  ";
		return s;
	}

	public QNode getParent() {
		return q_parent;
	}

	public Spring getParentSpring() {
		return q_parentSpring;
	}

	public Particle getNodeParticle() {
		return q_particle;
	}

	/**
	 * Autoboxing of 3DVector to native PVector
	 * 
	 * @return
	 */
	public PVector getNodePosition() {
		if (q_position == null)
			q_position = new PVector();
		q_position.x = q_particle.position.x;
		q_position.y = q_particle.position.y;
		q_position.z = q_particle.position.z;
		return q_position;
	}

	public int getPreferredLocation() {
		return q_preferredLocation;
	}

	public ArrayList<String> getSelects() {
		return q_selects;
	}

	public String getType() {
		return q_type;
	}

	// check behavior distanceSquaredTo
	public boolean hasMoved() {
		boolean moved = false;
		if (distanceSquaredTo(q_oldposition, getNodePosition()) > 3)
			moved = true;
		q_oldposition = new PVector(getNodePosition().x, getNodePosition().y,
				getNodePosition().z);
		return moved;
	}

	public boolean hasParentWithConstraints() {
		if (getParent() == null)
			return false;
		else if (getParent().getConstraints().size() > 0 
				|| getParent().getInstrinsicRole() != null)
			return true;
		else
			return getParent().hasParentWithConstraints();
	}

	public boolean isActive() {
		return q_active;
	}

	public boolean isExpanded() {
		return q_expanded;
	}

	public boolean isHanging() {
		return q_hanging;
	}

	public boolean isHidden() {
		return q_hidden;
	}

	public boolean isHovering() {
		return q_hovering;
	}

	public boolean isMultipleInstances() {
		return q_multipleInstances;
	}

	private boolean pointWithinEllipse(float posx, float posy, float radx,
			float rady, float px, float py) {

		float dx = (px - posx);
		float dy = (py - posy);

		return (dx * dx) / (radx * radx) + (dy * dy) / (rady * rady) <= 1;
	}

	public void removeChild(QNode child) {
		this.q_children.remove(child);
	}

	public void removeConstraint(String constraint) {
		if (!q_constraints.remove(constraint))
			if(getHiddenChild()!=null)
				getHiddenChild().removeConstraint(constraint);
		else if (q_constraints.size() + q_selects.size() == 0)
			setActive(false);
	}

	public void removeSelection(String s) {
		if (!q_selects.remove(s))
			getHiddenChild().removeSelection(s);
		else if (q_constraints.size() + q_selects.size() == 0)
			setActive(false);
	}

	public void replaceConstraint(String previousConstraint, String constraint) {
		if (!q_constraints.contains(previousConstraint))
			getHiddenChild().replaceConstraint(previousConstraint, constraint);
		else {
			q_constraints.remove(previousConstraint);
			q_constraints.add(constraint);
		}
	}

	public void replaceSelect(String previousSelect, String select) {
		if (!q_selects.contains(previousSelect))
			getHiddenChild().replaceSelect(previousSelect, select);
		else {
			q_selects.remove(previousSelect);
			q_selects.add(select);
		}
	}

	public void setAbbreviation(String abbreviation) {
		this.q_abbreviation = abbreviation;
	}

	public void setActive(boolean a) {
		q_active = a;
		spreadActivity(this,a);
	}

	public void setChildren(ArrayList<QNode> children) {
		this.q_children = children;
	}

	public void setConnector(String connector) {
		this.q_connector = connector;
	}

	public void setDepth(int depth) {
		this.q_depth = depth;
	}

	public void setExpanded(boolean expanded) {
		this.q_expanded = expanded;
	}

	public void setFields(ArrayList<String> fields) {
		this.q_fields = fields;
	}

	public void setFullName(String fullName) {
		this.q_fullName = fullName;
	}

	public void setHanging(boolean hanging) {
		this.q_hanging = hanging;
	}

	public void setHidden(boolean hidden) {
		this.q_hidden = hidden;
	}

	public void setHovering(boolean hovering) {
		this.q_hovering = hovering;
	}

	public void setMultipleInstances(boolean multipleInstances) {
		this.q_multipleInstances = multipleInstances;
	}

	public void setMultiplicity(int multiplicity) {
		this.q_multiplicity = multiplicity;
	}

	public void setName(String name) {
		this.q_name = name;
	}

	public void setParent(QNode parent) {
		this.q_parent = parent;
	}

	public void setParentSpring(Spring parentSpring) {
		this.q_parentSpring = parentSpring;
	}

	public void setParticle(Particle particle) {
		this.q_particle = particle;
	}

	public void setNodePosition(float x, float y, float z) {
		if (q_position == null)
			q_position = new PVector();
		q_position.x = x;
		q_position.y = y;
		q_position.z = z;
		q_particle.position = new PVector(x, y, z);
	}

	public void setPreferredLocation(int preferredLocation) {
		this.q_preferredLocation = preferredLocation;
	}

	public void setSelects(ArrayList<String> selects) {
		this.q_selects = selects;
	}

	public void setSelectSize(float selectSize) {

		this.q_selectSize = selectSize;
	}

	public void setType(String type) {
		this.q_type = type;
	}

	/**
	 * Spread the activity of a node (or inactivity) in the query graph. Will be
	 * called every time a node becomes active/inactive. Activity spreads from
	 * one node throughout the network, until another active node is found,
	 * which makes the previous one active. In turn, activity will spread back from that node, until all nodes on the shortest path between two active nodes are also active.
	 *
	 * The reverse happens if node becomes inactive: inactivity spreads until a 'truly' active node is found, making the previous one inactive, and starting the spread of inactivity from that node.
	 */
	private void spreadActivity(QNode q, boolean active) {
		//System.out.println("Activity ("+active+"): " + getFullName() + " " + getName() + " "+ isActive());
		ArrayList<QNode> targets = new ArrayList<QNode>();
		targets.addAll(getChildren());
		if(getParent()!=null)
			targets.add(getParent());
			
		for (QNode p : targets) {
		if (p != null && p != q) {
			if (active && p.isActive() && !isActive()){
				setActive(true);
				return;
			}
			else if (active)
				p.spreadActivity(this, active);
			else if(!active && p.isActive() && p.getConstraints().size()+p.getSelects().size()==0){
				p.setActive(false);
				}
			else if(!active && p.isActive()){
				p.setActive(true);
				return;
			}
		}
		}
	}
	
	public String toString() {
		String s = "";
		s += getOffset() + "<node name=\"" + getName().replace("\n", "_")
				+ "\" fullname=\"" + getFullName() + "\" abbreviation=\""
				+ getAbbreviation();
		if (getConnector() != null && getConnector().length() > 0)
			s += "\" connector=\"" + getConnector();
		s += "\" type=\"" + getType() + "\" location=\"";
		if (isHidden())
			s += "hidden\" >\n";
		else
			s += getNodeParticle().position.x + ","
					+ getNodeParticle().position.y + ","
					+ getNodeParticle().position.z + "\" >\n";
		for (String sel : getSelects())
			s += getOffset() + "  <select>" + sel + "</select>\n";
		for (String con : getConstraints())
			s += getOffset() + "  <constraint>" + con + "</constraint>\n";
		for (QNode q : getChildren())
			s += q.toString();
		s += getOffset() + "</node>\n";
		return s;
	}
}

public class QSystem extends ParticleSystem {

	private boolean s_hideRunNodes=true;
	private ArrayList<QNode> s_qNodes = new ArrayList<QNode>();
	private ArrayList<QNode> s_topNodes = new ArrayList<QNode>();
	
	public QSystem(float g, float somedrag) {
		super(g, somedrag);
	}

	public void addInnerChild(QNode n){
		//if (n.getAbbreviation().equals("cvr") || n.getAbbreviation().equals("ppr") || n.getAbbreviation().equals("bvr"))
			//n.addChild(new QNode(n, "Run","Run", "run", "rid:rid", "run", null));
		if (n.getAbbreviation().equals("ps"))
			n.addChild(new QNode(n, "Parameter","Input", "p", "input:fullName", "setup", null));
		else if (n.getAbbreviation().equals("aq") || n.getAbbreviation().equals("dq"))
			n.addChild(new QNode(n, "Quality","Quality", "q", "quality:name", "quality", null));
	}

	public void addMultiples(QNode q, QNode n){
		if ((n.getAbbreviation().equals("cvr") || n.getAbbreviation().equals("bvr")) && q.getName().equals("Evaluation"))
					q.setMultipleInstances(true);
		else if (n.getAbbreviation().equals("ls") && 
				(q.getName().equals("SubLearner") || q.getName().equals("Parameter") || q.getName().equals("SubFunction")))
						q.setMultipleInstances(true);
		else if ((n.getAbbreviation().equals("i") || n.getAbbreviation().equals("d")) && q.getName().equals("Quality"))
				q.setMultipleInstances(true);
		else if (n.getAbbreviation().equals("ppr") && q.getName().equals("Dataset"))
				q.setMultipleInstances(true);
		else if ((n.getAbbreviation().equals("cvs") || n.getAbbreviation().equals("fs")) && q.getName().equals("Parameter"))
			q.setMultipleInstances(true);
		else if (n.getAbbreviation().equals("exp") && q.getName().equals("Experiment Variable"))
				q.setMultipleInstances(true);
	}

	public void addSpacersToNode(QNode c, QNode p) {
		//makeAttraction(p, r, SPACER_STRENGTH, 10);
		for (QNode qn: s_qNodes) {
			Particle q = qn.getNodeParticle();
			if (c.getNodeParticle() != q && p.getNodeParticle() != q)
			  makeAttraction(c.getNodeParticle(), q, -1000, 1);
		}
	}



        public Spring makeEdgeBetween(Particle a, Particle b, double edgeLength) {
             return makeSpring(a, b, 0.002, 0.002, edgeLength);
        }

	/**
	 * Helper method. Eases conversion to javascript
	 *
	public ArrayList<Particle> getParticles(){
		return super.getParticles(); //won't work in javascript version
		//ArrayList<Particle> p = new ArrayList<Particle>();
		//for(int i=0; i<super.numberOfParticles(); i++)
		//	p.add(super.getParticle(i));
		//return p;
	}*/

	public void addTopNode(QNode topNode) {
		s_qNodes.add(topNode);
		s_topNodes.add(topNode);
	}
	
	public void buildGraph(String s){
            clear();
            cleanMore();
  
            QNode parentNode = null;
            for(String str : s.split("\n")){
			if (str.contains("<node")) {
				QNode q = new QNode(null,"","","","","",makeParticle());
				q.setPreferredLocation(-1);
				int i=-1;
				String[] ns = str.replace("<node","").replace(">","").split(" [^ ]+?=");
				q.setName(ns[1].replace("\"",""));
				q.setFullName(ns[2].replace("\"",""));
				q.setAbbreviation(ns[3].replace("\"",""));
				if(str.contains("connector")){
					i=0;
					q.setConnector(ns[4].replace("\"",""));
				}
				q.setType(ns[5+i].replace("\"",""));
				if(ns[6+i].contains(",")){
					String[] nsss= ns[6+i].replace("\"","").split(",");
					q.setNodePosition(float(nsss[0]),float(nsss[1]),float(nsss[2]));
				}else{
					q.setHidden(true);
					q.setParticle(parentNode.getNodeParticle());
				}
				int m = checkMultiplicity(q.getAbbreviation());
				if (m > 1)
					q.setMultiplicity(m);
				register(q, parentNode, true);
                                loadFields(q);
				parentNode = q;
			}
			else if(str.contains("</node>") && parentNode!=null){
				parentNode = parentNode.getParent();
			} else if (str.contains("select"))
				parentNode.addSelect(
						str.substring(str.indexOf("<select>") + 8,str.indexOf("</select>")));
			else if (str.contains("constraint"))
				parentNode.addConstraint(str.substring(str.indexOf("<constraint>") + 12,
								str.indexOf("</constraint>")));
		}
	}

	public int checkMultiplicity(String s) {
		int m = 1;
		for (QNode q : s_qNodes)
			if (q.getAbbreviation().equals(s))
				m++;
		return m;
	}

	public boolean childAllowed(QNode parent, String childName){
		//check if child already added, and if so whether a second with the same name is allowed
		for(QNode nq : parent.getChildren()){
			if(nq.getName().equals(childName) && !nq.isMultipleInstances()){
				//System.out.println("Child "+childName+" not allowed for "+parent.getName());
				return false;
			}
		}
		return true;
	}

	public void cleanMore() {
		s_topNodes = new ArrayList<QNode>();
		s_qNodes = new ArrayList<QNode>();
	}
	
	/**
	 * Collapses this node (hides its child nodes)
	 */
	public void collapse(QNode q) {
		ArrayList<QNode> delList = new ArrayList<QNode>();
		for (QNode n : q.getChildren())
			delList.add(n);
		for (QNode n : delList)
			removeQNode(n);
		q.setExpanded(false);
	}


	public QNode createQNode(QNode parent, String name, String fullname,
			String abbrev,  String connector,  String type, int degree){
		if(parent==null || childAllowed(parent,name)){
		Particle p = makeParticle();
		p.setMass(1f);
		QNode q = new QNode(parent, name, fullname, abbrev, connector, type, p);
		if(degree>=0)
			q.setPreferredLocation(degree);
		return q;}
		return null;
	}
	
	public void expand(QNode n) {
		ArrayList<QNode> newNodes = new ArrayList<QNode>();

		// expand children - according to ontology
		// CVRun
		if (n.getAbbreviation().equals("cvr")) {
			newNodes.add(createQNode(n, "Cross Validation", "Algorithm_Setup", "cvs", "CVSetup:sid", "setup", 0));
			newNodes.add(createQNode(n, "Learner", "Algorithm_Setup", "ls", "learner:sid", "setup", 180));
			newNodes.add(createQNode(n, "Dataset", "Dataset", "d", "inputdata:did","data:in", 270));
			newNodes.add(createQNode(n, "Evaluation", "Evaluation", "e", "rid:source","data:out", 90));
		} else if (n.getAbbreviation().equals("ls")) {
			newNodes.add(createQNode(n, "SubLearner", "Algorithm_Setup","ls", "sid:parent", "setup", 10));
			newNodes.add(createQNode(n, "Parameter","Input_Setting", "ps", "sid:setup", "setup", 20));
			newNodes.add(createQNode(n, "SubFunction","Function_Setup", "fs","sid:parent", "setup", 30));
			newNodes.add(createQNode(n, "Implementation","Implementation", "i", "implementation:fullname", "setup",40));
			//newNodes.add(createQNode(n, "Algorithm","Algorithm", "a", "algorithm:name", "setup",50));
		} else if (n.getAbbreviation().equals("i")) {
			newNodes.add(createQNode(n, "Quality","Algorithm_Quality", "aq", "fullname:implementation", "quality", 10));
			newNodes.add(createQNode(n, "Algorithm","Algorithm", "a", "implements:name", "setup",20));
		} else if (n.getAbbreviation().equals("d")) {
			newNodes.add(createQNode(n, "Quality","Data_Quality", "dq", "did:data", "quality", 0));
			newNodes.add(createQNode(n, "Data Processing", "PPRun","ppr", "did:outputData", "run", 270));
		} else if (n.getAbbreviation().equals("ppr")) {
			newNodes.add(createQNode(n, "Data Processing", "Algorithm_Setup","pps", "setup:sid", "setup", 0));
			newNodes.add(createQNode(n, "Dataset", "Dataset", "d", "inputdata:did", "data:in", 270));
		} else if (n.getAbbreviation().equals("bvr")) {
			newNodes.add(createQNode(n, "Bias-Variance Decomposition", "Algorithm_Setup","bvs", "BVSetup:sid", "setup", 0));
			newNodes.add(createQNode(n, "Learner", "Algorithm_Setup", "ls", "learner:sid", "setup", 180));
			newNodes.add(createQNode(n, "Dataset", "Dataset", "d", "inputdata:did","data:in", 270));
			newNodes.add(createQNode(n, "Evaluation", "Evaluation", "e", "rid:source","data:out", 90));
		} else if (n.getAbbreviation().equals("cvs")) {
			newNodes.add(createQNode(n, "CV Parameter","Input_Setting", "ps", "sid:setup", "setup", 350));
			newNodes.add(createQNode(n, "Implementation","Implementation", "i", "implementation:fullname", "setup", 10));
			//newNodes.add(createQNode(n, "CV Algorithm","Algorithm", "a", "algorithm:name", "setup", 20));
			expand(n.getParent());
		} else if (n.getAbbreviation().equals("pps") || n.getAbbreviation().equals("bvs")){
			newNodes.add(createQNode(n, "Parameter","Input_Setting", "ps", "sid:setup", "setup", 0));
			newNodes.add(createQNode(n, "Implementation","Implementation", "i", "implementation:fullname", "setup", 10));
			//newNodes.add(createQNode(n, "Algorithm","Algorithm", "a", "algorithm:name", "setup", 20));
			expand(n.getParent());
		} else if (n.getAbbreviation().equals("e")) {
			//newNodes.add(createQNode(n, "Evaluation function","MathFunction", "f","function:name", "setup", 90));
		} else if (n.getAbbreviation().equals("fs")) {
			newNodes.add(createQNode(n, "Parameter", "Input_Setting", "ps", "sid:setup", "setup", 0));
			//newNodes.add(createQNode(n, "Function","MathFunction", "f","function:name", "setup", 10));
		} else if (n.getAbbreviation().equals("exp")) {
			newNodes.add(createQNode(n, "Experiment Variable", "Experimental_Variable", "xv", "sid:experiment", "setup", 0));
			newNodes.add(createQNode(n, "Experiment Setup", "Algorithm_Setup", "ls", "setup:sid", "setup", 10));
			newNodes.add(createQNode(n, "Cross Validation", "CVRun", "cvr", "sid:experiment", "setup", 20));
			newNodes.add(createQNode(n, "Bias-Variance Decomposition", "BVRun", "bvr", "sid:experiment", "setup", 30));
		} 

		int siblingCount=0;
		for (QNode q : newNodes) {
			if(q!=null)
				siblingCount++;
		}
		for (QNode q : newNodes) {
			if(q!=null){
				int m = checkMultiplicity(q.getAbbreviation());
				if (m > 1)
					q.setMultiplicity(m);
				//if more than one option, make nodes hanging
				if(siblingCount>1 || n.getType().equals("run"))
					q.setHanging(true);
				///if a run node, automatically add all children, unless it already existed (multi-instance)
				if(n.getType().equals("run") && n.getChild(q.getName())==null)
					q.setHanging(false);
				register(q, n, false);
				}
		}
		n.setExpanded(true);
	}


	public ArrayList<QNode> getAllNodes() {
		ArrayList<QNode> ns = new ArrayList<QNode>();
		ns.addAll(s_qNodes);
		for(QNode q : s_qNodes)
			if(q.getHiddenChild() != null)
				ns.add(q.getHiddenChild());
		return ns;
	}

	public ArrayList<QNode> getQNodes() {
		return s_qNodes;
	}

	public ArrayList<QNode> getTopNodes() {
		return s_topNodes;
	}
	
	public QNode makeTopQNode(String name, String fullname, String abbr,  String type) {
		QNode p = createQNode(null, name, fullname, abbr, "", type, 0);
		p.setDepth(0);
		p.setNodePosition(0f, 0f, 0f);
		addTopNode(p);
		return p;
	}
	
	/** 
	 * Show underlying children for a node (for the help function)
	 * 
	 * @param n
	 * @return
	 */
	public ArrayList<String> previewChildren(QNode n) {
		ArrayList<String> children = new ArrayList<String>();

		if (n.getAbbreviation().equals("cvr")) {
			if(!s_hideRunNodes)
				children.add("Cross Validation (setup)");
			children.add("Learner (setup)");
			children.add("Dataset (input)");
			children.add("Evaluation (output)");
		} else if (n.getAbbreviation().equals("ls")) {
			children.add("SubLearner");
			children.add("Parameter");
			children.add("SubFunction");
			children.add("Implementation");
			//children.add("Algorithm");
		} else if (n.getAbbreviation().equals("i")) {
			children.add("Quality");
			children.add("Algorithm");
		} else if (n.getAbbreviation().equals("d")) {
			children.add("Quality");
			children.add("Data Processing");
		} else if (n.getAbbreviation().equals("ppr")) {
			if(!s_hideRunNodes)
				children.add("Data Processing (setup)");
			children.add("Dataset");
		} else if (n.getAbbreviation().equals("bvr")) {
			if(!s_hideRunNodes)
				children.add("Bias-Variance Decomposition (setup)");
			children.add("Learner (setup)");
			children.add("Dataset (input)");
			children.add("Evaluation (output)");
		} else if (n.getAbbreviation().equals("cvs")){
			children.add("CV Parameter");
			children.add("Implementation");
			//children.add("CV Algorithm");
		} else if (n.getAbbreviation().equals("pps") || n.getAbbreviation().equals("bvs")) {
			children.add("Parameter");
			children.add("Implementation");
			//children.add("Algorithm");
		} else if (n.getAbbreviation().equals("e")) {
			//children.add("Evaluation function");
		} else if (n.getAbbreviation().equals("fs")) {
			children.add("Parameter");
			//children.add("Function");
		} else if (n.getAbbreviation().equals("exp")) {
			children.add("Experiment Variable");
			children.add("Experiment Setup");
			children.add("Cross Validation");
			children.add("Bias-Variance Decomposition");
		}
		//add children of hidden run node
		if(s_hideRunNodes && n.getParent()!=null && n.getType().equals("setup") && 
				n.getParent().getType().equals("run") && n.getName().equals(n.getParent().getName())){
			children.addAll(previewChildren(n.getParent()));			
		}
		
		//remove children that are already added
		for (String c : new ArrayList<String>(children)){
			String name = c.split(" \\(")[0];
			//child already exists and there can not be multiples
			if (n.getChild(name)!=null && !n.getChild(name).isMultipleInstances())
				children.remove(c);
			//child already exists for a hidden run node
			else if(s_hideRunNodes && n.getType().equals("setup") && n.getParent()!=null && n.getParent().getName().equals(n.getName()) &&
					n.getParent().getChild(name)!=null && !n.getParent().getChild(name).isMultipleInstances())
				children.remove(c);
		}
		return children;
	}

	/**
	 * Registers node q as the child of node n
	 * 
	 * @param q
	 * @param n
	 */
	public void register(QNode q, QNode n, boolean building) {
		if (n != null) {
			n.addChild(q);
			if(q.getParent()==null)
				q.setParent(n);
			if(!building)
				addInnerChild(q);
			addMultiples(q,n);
			
			//check if run must be hidden
			if(s_hideRunNodes && q.getName().equals(n.getName()) && q.getType().equals("setup") && n.getType().equals("run"))
				q.setParticle(n.getNodeParticle());
				
			//Add node to list, add edges
			s_qNodes.add(q);
                        float edgeLength=100;
                        //if(q.getType().equals("setup"))
                          //edgeLength/=2;
			q.setParentSpring(makeEdgeBetween(q.getNodeParticle(), n.getNodeParticle(),edgeLength));
                        if(q.getNodeParticle()!=n.getNodeParticle() )
                            addSpacersToNode(q, n);
      
			
			//Set location
			if (q.getPreferredLocation() >= 0){
				//get distance of other children
				float d= 0.001F;
				if(n.getChildren().get(0)!=null)
					d += n.getNodeParticle().distanceTo(n.getChildren().get(0).getNodeParticle());
				q.setNodePosition(
						n.getNodePosition().x +
								+ ((float) (d*sin(q.getPreferredLocation()
										* PI / 180))),
						n.getNodePosition().y +
								- ((float) (d*cos(q.getPreferredLocation()
										* PI / 180))), 0f);
			}
		} else
			addTopNode(q);
	}
	
	/**
	 * Hides the node. The node collapses and is hidden.
	 */
	public void removeQNode(QNode q) {
		if(getTopNodes().contains(q)){
			for(QNode c : q.getChildren()){
				addTopNode(c);
				c.setParent(null);
			}
			removeEdges(q.getNodeParticle());
			removeParticle(q.getNodeParticle());
			removeTopNode(q);
		}
		else{
			collapse(q);
			removeEdges(q.getNodeParticle());
			if (q.getParent() != null)
				q.getParent().removeChild(q);
			removeParticle(q.getNodeParticle());
			s_qNodes.remove(q);
		}
	}
	

	public void removeEdges(Particle a) {
		boolean changed=true;
		while(changed){
			changed=false;
		for (int i = 0; i < numberOfSprings(); i++)
			if (getSpring(i).getOneEnd() == a
					|| getSpring(i).getTheOtherEnd() == a){
				removeSpring(i);
				changed=true;
			}

		for (int i = 0; i < numberOfAttractions(); i++)
			if (getAttraction(i).getOneEnd() == a
					|| getAttraction(i).getTheOtherEnd() == a){
				removeAttraction(i);
				changed=true;
			}
		}
	}

	public void removeHangingNodes(){
		for (QNode c : (ArrayList<QNode>) getQNodes().clone())
			if (c.isHanging())
				removeQNode(c);
	}
	
	public void removeTopNode(QNode q) {
		s_topNodes.remove(q);
		s_qNodes.remove(q);
		removeEdges(q.getNodeParticle());
		removeParticle(q.getNodeParticle());
		for(QNode qn : getQNodes())
			if(qn.getParent()==null && !getTopNodes().contains(qn))
				s_topNodes.add(qn);
		if(getTopNodes().size()>1)
			removeTopNode(getTopNodes().get(getTopNodes().size()-1));
	}

	public void setQNodes(ArrayList<QNode> qNodes) {
		this.s_qNodes = qNodes;
	}
}


// Traer Physics 3.0
// Terms from Traer's download page, http://traer.cc/mainsite/physics/
//   LICENSE - Use this code for whatever you want, just send me a link jeff@traer.cc
//
// traer3a_01.pde 
//   From traer.physics - author: Jeff Traer
//     Attraction              Particle                     
//     EulerIntegrator         ParticleSystem  
//     Force                   RungeKuttaIntegrator         
//     Integrator              Spring
//     ModifiedEulerIntegrator Vector3D          
//
//   From traer.animator - author: Jeff Traer   
//     Smoother                                       
//     Smoother3D                  
//     Tickable     
//
//   New code - author: Carl Pearson
//     UniversalAttraction
//     Pulse
//

// 13 Dec 2010: Copied 3.0 src from http://traer.cc/mainsite/physics/ and ported to Processingjs,
//              added makeParticle2(), makeAttraction2(), replaceAttraction(), and removeParticle(int) -mrn (Mike Niemi)
//  9 Feb 2011: Fixed bug in Euler integrators where they divided by time instead of 
//              multiplying by it in the update steps,
//              eliminated the Vector3D class (converting the code to use the native PVector class),
//              did some code compaction in the RK solver,
//              added a couple convenience classes, UniversalAttraction and Pulse, simplifying 
//              the Pendulums sample (renamed to dynamics.pde) considerably. -cap (Carl Pearson)
// 24 Mar 2011: Changed the switch statement in ParticleSystem.setIntegrator() to an if-then-else
//              to avoid an apparent bug introduced in Processing-1.1.0.js where the 
//              variable, RUNGE_KUTTA, was not visible inside the switch statement.
//              Changed ModifiedEulerIntegrator to use the documented PVector interfaces to work with pjs. -mrn

//===========================================================================================
//                                      Attraction
//===========================================================================================
// attract positive repel negative
//package traer.physics;
public class Attraction implements Force
{
  Particle one;
  Particle b;
  float k;
  boolean on = true;
  float distanceMin;
  float distanceMinSquared;
	
  public Attraction( Particle a, Particle b, float k, float distanceMin )
  {
    this.one = a;
    this.b = b;
    this.k = k;
    this.distanceMin = distanceMin;
    this.distanceMinSquared = distanceMin*distanceMin;
  }

  protected void        setA( Particle p )            { one = p; }
  protected void        setB( Particle p )            { b = p; }
  public final float    getMinimumDistance()          { return distanceMin; }
  public final void     setMinimumDistance( float d ) { distanceMin = d; distanceMinSquared = d*d; }
  public final void     turnOff()                     { on = false; }
  public final void     turnOn()	              { on = true;  }
  public final void     setStrength( float k )        { this.k = k; }
  public final Particle getOneEnd()                   { return one; }
  public final Particle getTheOtherEnd()              { return b; }
  
  public void apply() 
  { if ( on && ( one.isFree() || b.isFree() ) )
      {
        PVector a2b = PVector.sub(one.position, b.position, new PVector());
        float a2bDistanceSquared = a2b.dot(a2b);

	if ( a2bDistanceSquared < distanceMinSquared )
	   a2bDistanceSquared = distanceMinSquared;

	float force = k * one.mass0 * b.mass0 / (a2bDistanceSquared * (float)Math.sqrt(a2bDistanceSquared));

        a2b.mult( force );

	// apply
        if ( b.isFree() )
	   b.force.add( a2b );	
        if ( one.isFree() ) {
           a2b.mult(-1f);
	   one.force.add( a2b );
        }
      }
  }

  public final float   getStrength() { return k; }
  public final boolean isOn()        { return on; }
  public final boolean isOff()       { return !on; }
} // Attraction

//===========================================================================================
//                                    UniversalAttraction
//===========================================================================================
// attract positive repel negative
public class UniversalAttraction implements Force {
  public UniversalAttraction( float k, float distanceMin, ArrayList targetList )
  {
    this.k = k;
    this.distanceMin = distanceMin;
    this.distanceMinSquared = distanceMin*distanceMin;
    this.targetList = targetList;
  }
  
  float k;
  boolean on = true;
  float distanceMin;
  float distanceMinSquared;
  ArrayList targetList;
  public final float    getMinimumDistance()          { return distanceMin; }
  public final void     setMinimumDistance( float d ) { distanceMin = d; distanceMinSquared = d*d; }
  public final void     turnOff()                     { on = false; }
  public final void     turnOn()	              { on = true;  }
  public final void     setStrength( float k )        { this.k = k; }
  public final float   getStrength() { return k; }
  public final boolean isOn()        { return on; }
  public final boolean isOff()       { return !on; }

  
  public void apply() 
  { 
    if ( on ) {
        for (int i=0; i < targetList.size(); i++ ) {
          for (int j=i+1; j < targetList.size(); j++) {
            Particle a = (Particle)targetList.get(i);
            Particle b = (Particle)targetList.get(j);
            if ( a.isFree() || b.isFree() ) {
              PVector a2b = PVector.sub(a.position, b.position, new PVector());
              float a2bDistanceSquared = a2b.dot(a2b);
              if ( a2bDistanceSquared < distanceMinSquared )
              a2bDistanceSquared = distanceMinSquared;
              float force = k * a.mass0 * b.mass0 / (a2bDistanceSquared * (float)Math.sqrt(a2bDistanceSquared));
              a2b.mult( force );

              if ( b.isFree() ) b.force.add( a2b );	
              if ( a.isFree() ) {
                 a2b.mult(-1f);
      	         a.force.add( a2b );
              }
            }
          }
        }
    }
  }
} //UniversalAttraction

//===========================================================================================
//                                    Pulse
//===========================================================================================
public class Pulse implements Force {
  public Pulse( float k, float distanceMin, PVector origin, float lifetime, ArrayList targetList )
  {
    this.k = k;
    this.distanceMin = distanceMin;
    this.distanceMinSquared = distanceMin*distanceMin;
    this.origin = origin;
    this.targetList = targetList;
    this.lifetime = lifetime;
  }
  
  float k;
  boolean on = true;
  float distanceMin;
  float distanceMinSquared;
  float lifetime;
  PVector origin;
  ArrayList targetList;
  
  public final void     turnOff() { on = false; }
  public final void     turnOn()  { on = true;  }
  public final boolean  isOn()    { return on; }
  public final boolean  isOff()   { return !on; }
  public final boolean  tick( float time ) { 
    lifetime-=time; 
    if (lifetime <= 0f) turnOff(); 
    return on;
  }
  
  public void apply() {
    if (on) {
      PVector holder = new PVector();
      int count = 0;
      for (Iterator i = targetList.iterator(); i.hasNext(); ) {
        Particle p = (Particle)i.next();
        if ( p.isFree() ) {
          holder.set( p.position.x, p.position.y, p.position.z );
          holder.sub( origin );
          float distanceSquared = holder.dot(holder);
          if (distanceSquared < distanceMinSquared) distanceSquared = distanceMinSquared;
          holder.mult(k / (distanceSquared * (float)Math.sqrt(distanceSquared)) );
          p.force.add( holder );
        }
      }
    }
  }
}//Pulse

//===========================================================================================
//                                      EulerIntegrator
//===========================================================================================
//package traer.physics;
public class EulerIntegrator implements Integrator
{
  ParticleSystem s;
	
  public EulerIntegrator( ParticleSystem s ) { this.s = s; }
  public void step( float t )
  {
    s.clearForces();
    s.applyForces();
		
    for ( Iterator i = s.particles.iterator(); i.hasNext(); )
      {
	Particle p = (Particle)i.next();
	if ( p.isFree() )
          {
	    p.velocity.add( PVector.mult(p.force, t/p.mass0) );
	    p.position.add( PVector.mult(p.velocity, t) );
	  }
      }
  }
} // EulerIntegrator

//===========================================================================================
//                                          Force
//===========================================================================================
// May 29, 2005
//package traer.physics;
// @author jeffrey traer bernstein
public interface Force
{
  public void    turnOn();
  public void    turnOff();
  public boolean isOn();
  public boolean isOff();
  public void    apply();
} // Force

//===========================================================================================
//                                      Integrator
//===========================================================================================
//package traer.physics;
public interface Integrator 
{
  public void step( float t );
} // Integrator

//===========================================================================================
//                                    ModifiedEulerIntegrator
//===========================================================================================
//package traer.physics;
public class ModifiedEulerIntegrator implements Integrator
{
  ParticleSystem s;
  public ModifiedEulerIntegrator( ParticleSystem s ) { this.s = s; }
  public void step( float t )
  {
    s.clearForces();
    s.applyForces();
		
    float halft = 0.5f*t;
//    float halftt = 0.5f*t*t;
    PVector a = new PVector();
    PVector holder = new PVector();
    
    for ( int i = 0; i < s.numberOfParticles(); i++ )
      {
	Particle p = s.getParticle( i );
	if ( p.isFree() )
	  { // The following "was"s was the code in traer3a which appears to work in the IDE but not pjs
            // I couln't find the interface Carl used in the PVector documentation and have converted
            // the code to the documented interface. -mrn
            
            // was in traer3a: PVector.div(p.force, p.mass0, a);
            a.set(p.force.x, p.force.y, p.force.z);
            a.div(p.mass0);

	    //was in traer3a: p.position.add( PVector.mult(p.velocity, t, holder) );
            holder.set(p.velocity.x, p.velocity.y, p.velocity.z);
            holder.mult(t);
            p.position.add(holder);

	    //was in traer3a: p.position.add( PVector.mult(a, halft, a) );
            holder.set(a.x, a.y, a.z);
            holder.mult(halft); // Note that the original Traer code used halftt ( 0.5*t*t ) here -mrn
            p.position.add(holder);

            //was in traer3a: p.velocity.add( PVector.mult(a, t, a) );
            holder.set(a.x, a.y, a.z);
            holder.mult(t);
            p.velocity.add(a);
	  }
      }
  }
} // ModifiedEulerIntegrator

//===========================================================================================
//                                         Particle
//===========================================================================================
//package traer.physics;
public class Particle
{
  PVector position = new PVector();
  PVector velocity = new PVector();
  PVector force = new PVector();
  protected float    mass0;
  protected float    age0 = 0;
  protected boolean  dead0 = false;
  boolean            fixed0 = false;
	
  public Particle( float m )
  { mass0 = m; }
  
  // @see traer.physics.AbstractParticle#distanceTo(traer.physics.Particle)
  public final float distanceTo( Particle p ) { return this.position.dist( p.position ); }
  
  // @see traer.physics.AbstractParticle#makeFixed()
  public final Particle makeFixed() {
    fixed0 = true;
    velocity.set(0f,0f,0f);
    force.set(0f, 0f, 0f);
    return this;
  }
  
  // @see traer.physics.AbstractParticle#makeFree()
  public final Particle makeFree() {
    fixed0 = false;
    return this;
  }

  // @see traer.physics.AbstractParticle#isFixed()
  public final boolean isFixed() { return fixed0; }
  
  // @see traer.physics.AbstractParticle#isFree()
  public final boolean isFree() { return !fixed0; }
    
  // @see traer.physics.AbstractParticle#mass()
  public final float mass() { return mass0; }
  
  // @see traer.physics.AbstractParticle#setMass(float)
  public final void setMass( float m ) { mass0 = m; }
    
  // @see traer.physics.AbstractParticle#age()
  public final float age() { return age0; }
  
  protected void reset()
  {
    age0 = 0;
    dead0 = false;
    position.set(0f,0f,0f);
    velocity.set(0f,0f,0f);
    force.set(0f,0f,0f);
    mass0 = 1f;
  }
} // Particle

//===========================================================================================
//                                      ParticleSystem
//===========================================================================================
// May 29, 2005
//package traer.physics;
//import java.util.*;
public class ParticleSystem
{
  public static final int RUNGE_KUTTA = 0;
  public static final int MODIFIED_EULER = 1;
  protected static final float DEFAULT_GRAVITY = 0;
  protected static final float DEFAULT_DRAG = 0.001f;	
  ArrayList  particles = new ArrayList();
  ArrayList  springs = new ArrayList();
  ArrayList  attractions = new ArrayList();
  ArrayList  customForces = new ArrayList();
  ArrayList  pulses = new ArrayList();
  Integrator integrator;
  PVector    gravity = new PVector();
  float      drag;
  boolean    hasDeadParticles = false;
  
  public final void setIntegrator( int which )
  {
    //switch ( which )
    //{
    //  case RUNGE_KUTTA:
    //	  this.integrator = new RungeKuttaIntegrator( this );
    //	  break;
    //  case MODIFIED_EULER:
    //	  this.integrator = new ModifiedEulerIntegrator( this );
    //	  break;
    //}
    if ( which==RUNGE_KUTTA )
       this.integrator = new RungeKuttaIntegrator( this );
    else
    if ( which==MODIFIED_EULER )
       this.integrator = new ModifiedEulerIntegrator( this );
  }
  
  public final void setGravity( float x, float y, float z ) { gravity.set( x, y, z ); }

  // default down gravity
  public final void     setGravity( float g ) { gravity.set( 0, g, 0 ); }
  public final void     setDrag( float d )    { drag = d; }
  public final void     tick()                { tick( 1 ); }
  public final void     tick( float t )       {
    integrator.step( t );
    for (int i = 0; i<pulses.size(); ) {
    	Pulse p = (Pulse)pulses.get(i);
    	p.tick(t);
    	if (p.isOn()) { i++; } else { pulses.remove(i); }
    }
    if (pulses.size()!=0) for (Iterator i = pulses.iterator(); i.hasNext(); ) {
      Pulse p = (Pulse)(i.next());
      p.tick( t );
      if (!p.isOn()) i.remove();
    }
  }
  
  public final Particle makeParticle( float mass, float x, float y, float z )
  {
    Particle p = new Particle( mass );
    p.position.set( x, y, z );
    particles.add( p );
    return p;
  }
  
  public final int makeParticle2( float mass, float x, float y, float z )
  { // mrn
    makeParticle(mass, x, y, z);
    return particles.size()-1;
  }
  
  public final Particle makeParticle() { return makeParticle( 1.0f, 0f, 0f, 0f ); }
  
  public final Spring   makeSpring( Particle a, Particle b, float ks, float d, float r )
  {
    Spring s = new Spring( a, b, ks, d, r );
    springs.add( s );
    return s;
  }
  
  public final Attraction makeAttraction( Particle first, Particle b, float k, float minDistance )
  {
    Attraction m = new Attraction( first, b, k, minDistance );
    attractions.add( m );
    return m;
  }
  
  public final int makeAttraction2( Particle a, Particle b, float k, float minDistance )
  { // mrn
    makeAttraction(a, b, k, minDistance);
    return attractions.size()-1; // return the index 
  }

  public final void replaceAttraction( int i, Attraction m )
  { // mrn
    attractions.set( i, m );
  }  

  public final void addPulse(Pulse pu){ pulses.add(pu); }

  public final void clear()
  {
    particles.clear();
    springs.clear();
    attractions.clear();
    customForces.clear();
    pulses.clear();
  }
  
  public ParticleSystem( float g, float somedrag )
  {
    setGravity( 0f, g, 0f );
    drag = somedrag;
    integrator = new RungeKuttaIntegrator( this );
  }
  
  public ParticleSystem( float gx, float gy, float gz, float somedrag )
  {
    setGravity( gx, gy, gz );
    drag = somedrag;
    integrator = new RungeKuttaIntegrator( this );
  }
  
  public ParticleSystem()
  {
  	setGravity( 0f, ParticleSystem.DEFAULT_GRAVITY, 0f );
    drag = ParticleSystem.DEFAULT_DRAG;
    integrator = new RungeKuttaIntegrator( this );
  }
  
  protected final void applyForces()
  {
    if ( gravity.mag() != 0f )
      {
        for ( Iterator i = particles.iterator(); i.hasNext(); )
	  {
            Particle p = (Particle)i.next();
            if (p.isFree()) p.force.add( gravity );
	  }
      }
      
    PVector target = new PVector();
    for ( Iterator i = particles.iterator(); i.hasNext(); )
      {
        Particle p = (Particle)i.next();
        if (p.isFree()) p.force.add( PVector.mult(p.velocity, -drag, target) );

      }
      
    applyAll(springs);
    applyAll(attractions);
    applyAll(customForces);
    applyAll(pulses);
      
    
  }
  
  private void applyAll(ArrayList forces) {
    if( forces.size()!=0 ) for ( Iterator i = forces.iterator(); i.hasNext(); ) ((Force)i.next()).apply();
  }
  
  protected final void clearForces()
  {
    for (Iterator i = particles.iterator(); i.hasNext(); ) ((Particle)i.next()).force.set(0f, 0f, 0f);
  }
  
  public final int        numberOfParticles()              { return particles.size(); }
  public final int        numberOfSprings()                { return springs.size(); }
  public final int        numberOfAttractions()            { return attractions.size(); }
  public final Particle   getParticle( int i )             { return (Particle)particles.get( i ); }
  public final Spring     getSpring( int i )               { return (Spring)springs.get( i ); }
  public final Attraction getAttraction( int i )           { return (Attraction)attractions.get( i ); }
  public final void       addCustomForce( Force f )        { customForces.add( f ); }
  public final int        numberOfCustomForces()           { return customForces.size(); }
  public final Force      getCustomForce( int i )          { return (Force)customForces.get( i ); }
  public final Force      removeCustomForce( int i )       { return (Force)customForces.remove( i ); }
  public final void       removeParticle( int i )          { particles.remove( i ); } //mrn
  public final void       removeParticle( Particle p )     { particles.remove( p ); }
  public final Spring     removeSpring( int i )            { return (Spring)springs.remove( i ); }
  public final Attraction removeAttraction( int i )        { return (Attraction)attractions.remove( i ); }
  public final void       removeAttraction( Attraction s ) { attractions.remove( s ); }
  public final void       removeSpring( Spring a )         { springs.remove( a ); }
  public final void       removeCustomForce( Force f )     { customForces.remove( f ); }
} // ParticleSystem

//===========================================================================================
//                                      RungeKuttaIntegrator
//===========================================================================================
//package traer.physics;
//import java.util.*;
public class RungeKuttaIntegrator implements Integrator
{	
  ArrayList originalPositions = new ArrayList();
  ArrayList originalVelocities = new ArrayList();
  ArrayList k1Forces = new ArrayList();
  ArrayList k1Velocities = new ArrayList();
  ArrayList k2Forces = new ArrayList();
  ArrayList k2Velocities = new ArrayList();
  ArrayList k3Forces = new ArrayList();
  ArrayList k3Velocities = new ArrayList();
  ArrayList k4Forces = new ArrayList();
  ArrayList k4Velocities = new ArrayList();
  ParticleSystem s;

  public RungeKuttaIntegrator( ParticleSystem s ) { this.s = s;	}
  
  private final void allocateParticles()
  {
    while( s.particles.size() > originalPositions.size() ) {
        originalPositions.add( new PVector() );
		originalVelocities.add( new PVector() );
		k1Forces.add( new PVector() );
		k1Velocities.add( new PVector() );
		k2Forces.add( new PVector() );
		k2Velocities.add( new PVector() );
		k3Forces.add( new PVector() );
		k3Velocities.add( new PVector() );
		k4Forces.add( new PVector() );
		k4Velocities.add( new PVector() );
    }
  }
  
  private final void setIntermediate(ArrayList forces, ArrayList velocities) {
    s.applyForces();
    for ( int i = 0; i < s.particles.size(); ++i )
      {
	Particle p = (Particle)s.particles.get( i );
	if ( p.isFree() )
	  {
	    ((PVector)forces.get( i )).set( p.force.x, p.force.y, p.force.z );
	    ((PVector)velocities.get( i )).set( p.velocity.x, p.velocity.y, p.velocity.z );
            p.force.set(0f,0f,0f);
	  }
      }
  }
  
  private final void updateIntermediate(ArrayList forces, ArrayList velocities, float multiplier) {
    PVector holder = new PVector();
    
    for ( int i = 0; i < s.particles.size(); ++i )
      {
	Particle p = (Particle)s.particles.get( i );
	if ( p.isFree() )
	  {
	  		PVector op = (PVector)(originalPositions.get( i ));
            p.position.set(op.x, op.y, op.z);
            p.position.add(PVector.mult((PVector)(velocities.get( i )), multiplier, holder));		
			PVector ov = (PVector)(originalVelocities.get( i ));
            p.velocity.set(ov.x, ov.y, ov.z);
            p.velocity.add(PVector.mult((PVector)(forces.get( i )), multiplier/p.mass0, holder));	
          }
       }
  }
  
  private final void initialize() {
    for ( int i = 0; i < s.particles.size(); ++i )
      {
	Particle p = (Particle)(s.particles.get( i ));
	if ( p.isFree() )
	  {		
	    ((PVector)(originalPositions.get( i ))).set( p.position.x, p.position.y, p.position.z );
	    ((PVector)(originalVelocities.get( i ))).set( p.velocity.x, p.velocity.y, p.velocity.z );
	  }
	p.force.set(0f,0f,0f);	// and clear the forces
      }
  }
  
  public final void step( float deltaT )
  {	
    allocateParticles();
    initialize();       
    setIntermediate(k1Forces, k1Velocities);
    updateIntermediate(k1Forces, k1Velocities, 0.5f*deltaT );
    setIntermediate(k2Forces, k2Velocities);
    updateIntermediate(k2Forces, k2Velocities, 0.5f*deltaT );
    setIntermediate(k3Forces, k3Velocities);
    updateIntermediate(k3Forces, k3Velocities, deltaT );
    setIntermediate(k4Forces, k4Velocities);
		
    /////////////////////////////////////////////////////////////
    // put them all together and what do you get?
    for ( int i = 0; i < s.particles.size(); ++i )
      {
	Particle p = (Particle)s.particles.get( i );
	p.age0 += deltaT;
	if ( p.isFree() )
	  {
	    // update position
	    PVector holder = (PVector)(k2Velocities.get( i ));
            holder.add((PVector)k3Velocities.get( i ));
            holder.mult(2.0f);
            holder.add((PVector)k1Velocities.get( i ));
            holder.add((PVector)k4Velocities.get( i ));
            holder.mult(deltaT / 6.0f);
            holder.add((PVector)originalPositions.get( i ));
            p.position.set(holder.x, holder.y, holder.z);
            							
	    // update velocity
	    holder = (PVector)k2Forces.get( i );
	    holder.add((PVector)k3Forces.get( i ));
            holder.mult(2.0f);
            holder.add((PVector)k1Forces.get( i ));
            holder.add((PVector)k4Forces.get( i ));
            holder.mult(deltaT / (6.0f * p.mass0 ));
            holder.add((PVector)originalVelocities.get( i ));
	    p.velocity.set(holder.x, holder.y, holder.z);
	  }
      }
  }
} // RungeKuttaIntegrator

//===========================================================================================
//                                         Spring
//===========================================================================================
// May 29, 2005
//package traer.physics;
// @author jeffrey traer bernstein
public class Spring implements Force
{
  float springConstant0;
  float damping0;
  float restLength0;
  Particle one, b;
  boolean on = true;
    
  public Spring( Particle A, Particle B, float ks, float d, float r )
  {
    springConstant0 = ks;
    damping0 = d;
    restLength0 = r;
    one = A;
    b = B;
  }
  
  public final void     turnOff()                { on = false; }
  public final void     turnOn()                 { on = true; }
  public final boolean  isOn()                   { return on; }
  public final boolean  isOff()                  { return !on; }
  public final Particle getOneEnd()              { return one; }
  public final Particle getTheOtherEnd()         { return b; }
  public final float    currentLength()          { return one.distanceTo( b ); }
  public final float    restLength()             { return restLength0; }
  public final float    strength()               { return springConstant0; }
  public final void     setStrength( float ks )  { springConstant0 = ks; }
  public final float    damping()                { return damping0; }
  public final void     setDamping( float d )    { damping0 = d; }
  public final void     setRestLength( float l ) { restLength0 = l; }
  
  public final void apply()
  {	
    if ( on && ( one.isFree() || b.isFree() ) )
      {
        PVector a2b = PVector.sub(one.position, b.position, new PVector());

        float a2bDistance = a2b.mag();	
	
	if (a2bDistance!=0f) {
          a2b.div(a2bDistance);
        }

	// spring force is proportional to how much it stretched 
	float springForce = -( a2bDistance - restLength0 ) * springConstant0; 
	
        PVector vDamping = PVector.sub(one.velocity, b.velocity, new PVector());
        
        float dampingForce = -damping0 * a2b.dot(vDamping);
		               				
	// forceB is same as forceA in opposite direction
	float r = springForce + dampingForce;
		
	a2b.mult(r);
	    
	if ( one.isFree() )
	   one.force.add( a2b );
	if ( b.isFree() )
	   b.force.add( PVector.mult(a2b, -1, a2b) );
      }
  }
  protected void setA( Particle p ) { one = p; }
  protected void setB( Particle p ) { b = p; }
} // Spring

//===========================================================================================
//                                       Smoother
//===========================================================================================
//package traer.animator;
public class Smoother implements Tickable
{
  public Smoother(float smoothness)                     { setSmoothness(smoothness);  setValue(0.0F); }
  public Smoother(float smoothness, float start)        { setSmoothness(smoothness); setValue(start); }
  public final void     setSmoothness(float smoothness) { a = -smoothness; gain = 1.0F + a; }
  public final void     setTarget(float target)         { input = target; }
  public void           setValue(float x)               { input = x; lastOutput = x; }
  public final float    getTarget()                     { return input; }
  public final void     tick()                          { lastOutput = gain * input - a * lastOutput; }
  public final float    getValue()                      { return lastOutput; }
  public float a, gain, lastOutput, input;
} // Smoother

//===========================================================================================
//                                      Smoother3D
//===========================================================================================
//package traer.animator;
public class Smoother3D implements Tickable
{
  public Smoother3D(float smoothness)
  {
    x0 = new Smoother(smoothness);
    y0 = new Smoother(smoothness);
    z0 = new Smoother(smoothness);
  }
  public Smoother3D(float initialX, float initialY, float initialZ, float smoothness)
  {
    x0 = new Smoother(smoothness, initialX);
    y0 = new Smoother(smoothness, initialY);
    z0 = new Smoother(smoothness, initialZ);
  }
  public final void setXTarget(float X) { x0.setTarget(X); }
  public final void setYTarget(float X) { y0.setTarget(X); }
  public final void setZTarget(float X) { z0.setTarget(X); }
  public final float getXTarget()       { return x0.getTarget(); }
  public final float getYTarget()       { return y0.getTarget(); }
  public final float getZTarget()       { return z0.getTarget(); }
  public final void setTarget(float X, float Y, float Z)
  {
    x0.setTarget(X);
    y0.setTarget(Y);
    z0.setTarget(Z);
  }
  public final void setValue(float X, float Y, float Z)
  {
    x0.setValue(X);
    y0.setValue(Y);
    z0.setValue(Z);
  }
  public final void setX(float X)  { x0.setValue(X); }
  public final void setY(float Y)  { y0.setValue(Y); }
  public final void setZ(float Z)  { z0.setValue(Z); }
  public final void setSmoothness(float smoothness)
  {
    x0.setSmoothness(smoothness);
    y0.setSmoothness(smoothness);
    z0.setSmoothness(smoothness);
  }
  public final void tick()         { x0.tick(); y0.tick(); z0.tick(); }
  public final float x()           { return x0.getValue(); }
  public final float y()           { return y0.getValue(); }
  public final float z()           { return z0.getValue(); }
  public Smoother x0, y0, z0;
} // Smoother3D

//===========================================================================================
//                                      Tickable
//===========================================================================================
//package traer.animator;
public interface Tickable
{
  public abstract void tick();
  public abstract void setSmoothness(float f);
} // Tickable

