package org.embulk.standards;

import java.util.List;
import org.embulk.config.ConfigSource;
import org.embulk.config.TaskSource;
import org.embulk.config.NextConfig;
import org.embulk.config.CommitReport;
import org.embulk.spi.Schema;
import org.embulk.spi.Page;
import org.embulk.spi.Exec;
import org.embulk.spi.OutputPlugin;
import org.embulk.spi.TransactionalPageOutput;

public class NullOutputPlugin
        implements OutputPlugin
{
    @Override
    public NextConfig transaction(ConfigSource config,
            Schema schema, int processorCount,
            OutputPlugin.Control control)
    {
        return resume(Exec.newTaskSource(), schema, processorCount, control);
    }

    public NextConfig resume(TaskSource taskSource,
            Schema schema, int processorCount,
            OutputPlugin.Control control)
    {
        control.run(taskSource);
        return Exec.newNextConfig();
    }

    public void cleanup(TaskSource taskSource,
            Schema schema, int processorCount,
            List<CommitReport> successCommitReports)
    { }

    @Override
    public TransactionalPageOutput open(TaskSource taskSource, Schema schema, int processorIndex)
    {
        return new TransactionalPageOutput() {
            public void add(Page page)
            {
                page.release();
            }

            public void finish() { }

            public void close() { }

            public void abort() { }

            public CommitReport commit()
            {
                return Exec.newCommitReport();
            }
        };
    }
}
