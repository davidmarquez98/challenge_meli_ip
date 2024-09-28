package commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import service.IpService;

@ShellComponent
public class IpCommands {

    private final IpService ipService;

    public IpCommands(IpService ipService) {
        this.ipService = ipService;
    }

    @ShellMethod("Trace IP address")
    public String traceIp(String ipAddress) {
        return ipService.traceIp(ipAddress);
    }
}
