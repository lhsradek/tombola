curl -L -O https://artifacts.elastic.co/downloads/beats/elastic-agent/elastic-agent-8.4.1-linux-x86_64.tar.gz
tar xzvf elastic-agent-8.4.1-linux-x86_64.tar.gz
cp elastic-agent.yml elastic-agent-8.4.1-linux-x86_64
cd elastic-agent-8.4.1-linux-x86_64
./elastic-agent install
cd ..
rm elastic-agent-8.4.1-linux-x86_64.tar.gz
