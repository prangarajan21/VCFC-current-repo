package com.pluribus.vcf.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Optional;
import static org.testng.Assert.assertTrue;
import com.browserstack.local.Local;
import com.jcabi.ssh.Shell;
import com.pluribus.vcf.test.IATest;
import com.jcabi.ssh.SSHByPassword;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
/**
 *
 * @author Haritha
 */
public class TestSetup {
   private RemoteWebDriver driver;
   private ResourceBundle bundle;
   Local bsLocal = new Local();
   
   @Parameters({"vcfIp","clean"})
   @BeforeSuite(alwaysRun = true)
   public void cleanLogs(String vcfIp,@Optional("1")String clean) throws IOException,InterruptedException {
	   if(Integer.parseInt(clean) == 1) {
		Shell sh1 = new Shell.Verbose(
	            new SSHByPassword(
	                vcfIp,
			22,
	                "vcf",
	                "changeme"
	            )
	        );
		String out1;
		out1 = new Shell.Plain(sh1).exec("rm  /home/vcf/srv/vcf/config/flowfilters.json");	
		out1 = new Shell.Plain(sh1).exec("rm  /home/vcf/srv/vcf/config/pcap-agent.properties");	
		out1 = new Shell.Plain(sh1).exec("rm  /home/vcf/srv/vcf/config/pcap-engine.json");
		out1 = new Shell.Plain(sh1).exec("rm  /home/vcf/srv/vcf/config/vcf-center.properties");
		out1 = new Shell.Plain(sh1).exec("rm  /home/vcf/srv/vcf/config/pcap_agents.properties");
		out1 = new Shell.Plain(sh1).exec("rm  /home/vcf/srv/vcf/config/pcap-file.json");
		out1 = new Shell.Plain(sh1).exec("rm  /home/vcf/srv/vcf/config/switch-details.json");
		out1 = new Shell.Plain(sh1).exec("rm  /home/vcf/srv/vcf/config/vcf-license.json");
		out1 = new Shell.Plain(sh1).exec("rm  /home/vcf/srv/vcf/config/vcf-user.properties");
		out1 = new Shell.Plain(sh1).exec("rm  /home/vcf/srv/vcf/config/vcf-maestro.properties");	
		out1 = new Shell.Plain(sh1).exec("rm  /home/vcf/srv/vcf/config/es_nodes.properties");	
		out1 = new Shell.Plain(sh1).exec("rm  /home/vcf/srv/vcf/config/es_node.json");	
		out1 = new Shell.Plain(sh1).exec("rm  /home/vcf/srv/vcf/config/vcf-mgr.properties");	
		out1 = new Shell.Plain(sh1).exec("rm  /home/vcf/srv/vcf/config/vcf-collector.json");
		out1 = new Shell.Plain(sh1).exec("/home/vcf/srv/vcf/bin/stop-vcfc.sh");
		Thread.sleep(10000);
		out1 = new Shell.Plain(sh1).exec("/home/vcf/srv/vcf/bin/start-vcfc.sh");
		Thread.sleep(30000);
	}
   }
   @Parameters({"vcfIp","browser","local","bsUserId","bsKey"}) 
   @BeforeClass(alwaysRun = true)
   public void initDriver(String vcfIp, String browser, @Optional("0")String local,@Optional("pratikdam1")String bsUserId, @Optional("uZCXEzKXwgzgzMr3G7R6") String bsKey) throws Exception{
	   if(Integer.parseInt(local)==1) {
		   startDriver(vcfIp,browser);
	   }
	   else {
		   startDriver(vcfIp,browser,bsUserId,bsKey); //Call browserstack test session
	   }
   }
   
   public void startDriver(String vcfIp,String browserName) {
	   if(browserName.equalsIgnoreCase("chrome")) {
	   		System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver");
	   		DesiredCapabilities handlSSLErr = DesiredCapabilities.chrome();     
	   		handlSSLErr.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
	   		driver=new ChromeDriver(handlSSLErr);
	   		driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
			driver.manage().window().maximize();
			driver.get("https:"+vcfIp);
	   } else if (browserName.equalsIgnoreCase("firefox")) {
		   /*
	   		//Class ProfilesIni details 
	   		 ProfilesIni allProfiles = new ProfilesIni();
	   		 // Use FirefoxProfile Constructor
	   		 FirefoxProfile myProfile = allProfiles.getProfile("CertificateIssue");
	   		 myProfile.setAcceptUntrustedCertificates(true);
	   		 myProfile.setAssumeUntrustedCertificateIssuer(false);
	   		 driver = new FirefoxDriver(myProfile);
	   		*/
		    System.setProperty("webdriver.gecko.driver","src/test/resources/geckodriver");
	   		DesiredCapabilities caps = DesiredCapabilities.firefox();
	   	    caps.setCapability("marionette", true);
	   	    caps.setCapability("acceptInsecureCerts",true);
	   	    //var capabilities = new FirefoxOptions().addTo(caps);
	   	    System.out.println("Capabilities:"+caps.toString());
		    driver = new FirefoxDriver(caps);
	   		driver.get("https:"+vcfIp);
	   		System.out.println("title"+driver.getTitle());
	   }
	   //TODO: ADD IE AND SAFARI TO THE LIST
   }
 
    public void startDriver(String vcfIp,String browser,String bsUserId, String bsKey) throws Exception {
		HashMap<String,String> bsLocalArgs = new HashMap<String,String>();
		String sessionId = null;
		String command = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddhhmmss");
	    String dateAsString = simpleDateFormat.format(new Date());
	    String localId = "convergenceTest"+dateAsString;
		bsLocalArgs.put("localIdentifier",localId);
		bsLocalArgs.put("key",bsKey); //BrowserStack Key
		bsLocal.start(bsLocalArgs);
	    DesiredCapabilities caps = new DesiredCapabilities();
		caps.setCapability("browser",browser);
		caps.setCapability("build", "VCFC SmokeTest Cases");
		caps.setCapability("acceptSslCerts","true");
		caps.setCapability("browserstack.local", "true");
		caps.setCapability("browserstack.debug","true");
		caps.setCapability("browserstack.idleTimeout","150");
		caps.setCapability("platform","ANY");
		caps.setCapability("browserstack.localIdentifier",localId);
		driver = new RemoteWebDriver(
			      new URL("https://"+bsUserId+":"+bsKey+"@hub-cloud.browserstack.com/wd/hub"),
			      caps
			    );
		driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
        // Get a handle to the driver. This will throw an exception if a matching driver cannot be located
	    driver.get("https://"+ vcfIp);
	    com.jcabi.log.Logger.info("Logfile",getBSLogs(bsUserId,bsKey));
   }
   
   public String getBSLogs(String bsUserId,String bsKey) {
	    String sessId = driver.getSessionId().toString();
	    String url = "https://browserstack.com/automate/sessions/"+sessId+".json";
	    System.out.println("url:"+url.toString());
	    String authUser = bsUserId+":"+bsKey;
	    String encoding = Base64.encodeBase64String(authUser.getBytes());
	    Client restClient = Client.create();
        WebResource webResource = restClient.resource(url);
        ClientResponse resp = webResource.accept("application/json")
                                         .header("Authorization", "Basic " + encoding)
                                         .get(ClientResponse.class);
        if(resp.getStatus() != 200){
            System.err.println("Unable to connect to the server");
        }
        String output = resp.getEntity(String.class);
        JSONObject obj = new JSONObject(output);
        JSONObject bsLogs = (JSONObject) obj.get("automation_session");
        String publicUrl = bsLogs.get("public_url").toString();
        return publicUrl;
   }
   
   @AfterClass(alwaysRun = true)
    public void setupAfterSuite() throws Exception {
        //driver.close();
	    driver.quit();
    	bsLocal.stop();
    }
	
    public boolean isContainsText(String text) {
        return driver.getPageSource().contains(text);
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public String getPageSource() {
        return driver.getPageSource();
    }

    public void pageRefresh() {
        driver.navigate().refresh();
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String isRequired(String field) {
        String isrequired = getBundle().getString("errors.required").replace("{0}", field);
        return isrequired;
    }

    public String matchPattern(String field) {
        String isrequired = getBundle().getString("errors.required").replace("{0}", field);
        return isrequired;
    }
  
  
    @BeforeTest(alwaysRun = true)
    @Parameters({"resetSwitch"})
    //Run ansible reset switch script
  	public void resetSwitchAnsible (@Optional("1") String resetSwitch) throws Exception{
  		if(Integer.parseInt(resetSwitch) == 1) {
  			String out1;
  			StringBuffer output = null;
  		
  			String[] command = {"src/test/resources/resetSwitch.expect"};
  	        ProcessBuilder probuilder = new ProcessBuilder( command );

  	        //You can set up your work directory
  	        probuilder.directory(new File(System.getProperty("user.dir")));
  	        Process process = probuilder.start();
  	        
  	        //Read out dir output
  	        InputStream is = process.getInputStream();
  	        InputStreamReader isr = new InputStreamReader(is);
  	        BufferedReader br = new BufferedReader(isr);
  	        String line;
  	        System.out.printf("Output of running %s is:\n",
  	                Arrays.toString(command));
  	        while ((line = br.readLine()) != null) {
  	            System.out.println(line);
  	        }
  	        
  	        //Wait to get exit value
  	        try {
  	            int exitValue = process.waitFor();
  	            System.out.println("\n\nExit Value is " + exitValue);
  	        } catch (InterruptedException e) {
  	            // TODO Auto-generated catch block
  	            e.printStackTrace();
  	        }
  		}
  		return;
  	}

    /*Routine for logging. So any changes to this routine will be a single point of edit for all log messages */
    public void printLogs (String level, String msg1, String msg2) {
 	   if(level.equalsIgnoreCase("error")) {
 		   com.jcabi.log.Logger.error(msg1,msg2);
 	   } 
 	   if(level.equalsIgnoreCase("info")) {
 		   com.jcabi.log.Logger.info(msg1, msg2);
 	   }
 	   System.out.println(level+": "+ msg1 +" "+msg2);
    }
    
}

