# Netherblade

Netherblade allows you to look behind the curtain and see requests executed by the LCU layer of the Client.

---

### software requirements

* java 8

---

### Running the project

when running this project for the first time you are required to generate two files, this can either be done by hand or by running Netherblade with specific arguments.


to generate `system.json` run Netherblade using  
`--yaml "path-to-league-system.yaml"`


to generate `netherblade-config.json` run Netherblade using  
`--init`


afterwards you can launch Netherblade and redirect output to a file to view requests done by the LCU.
