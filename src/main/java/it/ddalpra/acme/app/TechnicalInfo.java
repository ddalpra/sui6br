package it.ddalpra.acme.app;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemProperties;
import org.omnifaces.util.Faces;

import it.ddalpra.acme.util.VirtualMachine;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import lombok.Data;
import lombok.extern.jbosslog.JBossLog;

@ApplicationScoped
@Named
@Data
@JBossLog
public class TechnicalInfo {

    private String quarkus;
    private String omniFaces;
    private String primeFaces;
    private String primeFacesExt;
    private String facesImpl;
    private String server;
    private String jvmRuntime;
    private String jvmVersion;
    private String jvmVendor;
    private String os;

    @PostConstruct
    protected void initialize() {
        quarkus = "Quarkus: " + StringUtils.defaultIfEmpty(
                io.quarkus.bootstrap.runner.QuarkusEntryPoint.class.getPackage().getImplementationVersion(), "???");
        omniFaces = "OmniFaces: " + StringUtils.defaultIfEmpty(
                org.omnifaces.util.Faces.class.getPackage().getImplementationVersion(), "???");
        // PF version
        primeFaces = "PrimeFaces: " + StringUtils.defaultIfEmpty(
                org.primefaces.util.Constants.class.getPackage().getImplementationVersion(), "???");
        primeFacesExt = "PrimeFaces Extensions: " + StringUtils
                .defaultIfEmpty(org.primefaces.extensions.util.Constants.class.getPackage()
                        .getImplementationVersion(), "???");
        facesImpl = StringUtils.removeIgnoreCase(StringUtils.removeIgnoreCase(Faces.getImplInfo(), "Core"), "Impl");
        server = Faces.getServerInfo();
        jvmRuntime = SystemProperties.getJavaRuntimeName();
        jvmVersion = SystemProperties.getJavaRuntimeVersion();
        jvmVendor = SystemProperties.getJavaVendor();
        os = SystemProperties.getOsName() + "-" + SystemProperties.getOsVersion() + "-" + SystemProperties.getOsArch();
    }

    public String getMemory() {
        return VirtualMachine.getMemoryStatisticsAsString();
    }

}