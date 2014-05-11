package de.seerheinlab.micgwaf.test;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import de.seerheinlab.micgwaf.component.Component;

/**
 * Test component which renders "TestComponent2".
 */
public class TestComponent2 extends Component
{

  /** SerialVersionUID. */
  private static final long serialVersionUID = 1L;

  public TestComponent2(String id, Component parent)
  {
    super(id, parent);
  }

  @Override
  public List<? extends Component> getChildren()
  {
    return new ArrayList<>();
  }

  @Override
  public void render(Writer writer) throws IOException
  {
    writer.append("TestComponent2");
  }

}
