 require 'net/http'
 
class Api
  attr_accessor :url
  attr_accessor :uri
 
  def initialize
    @url = "http://localhost:3000/devices"
    @uri = URI.parse @url
  end
 
  # Create an employee using a predefined XML template as a REST request.
  def create(number="Default Name", name="9999", address="00")
    json_req = "{\"device\":{ \"number\":\"#{number}\",\"name\":\"#{name}\",\"address\":\"#{address}\"}}"
 
    request = Net::HTTP::Post.new(@url)
    request.add_field "Content-Type", "application/json"
    request.body = json_req
 
    http = Net::HTTP.new(@uri.host, @uri.port)
    response = http.request(request)
 
    response.body    
  end
 
  # Read can get all employees with no arguments or
  # get one employee with one argument.  For example:
  # api = Api.new
  # api.read 2 => one 
  def read(id=nil)
    request = Net::HTTP.new(@uri.host, @uri.port)
 
    if id.nil?
      response = request.get("#{@uri.path}.json")      
    else
      response = request.get("#{@uri.path}/#{id}.json")    
    end
 
    response.body
  end
 
  # Update an employee using a predefined XML template as a REST request.
  def update(id, name="Updated Name", extension=0000)
    xml_req ="{\"device\":{ \"id\":#{id},\"number\":\"#{number}\",\"name\":\"#{name}\",\"address\":\"#{address}\"}}"
 
 
    request = Net::HTTP::Put.new("#{@url}/#{id}.json")
    request.add_field "Content-Type", "application/json"
    request.body = xml_req
 
    http = Net::HTTP.new(@uri.host, @uri.port)
    response = http.request(request)
 
    # no response body will be returned
    case response
    when Net::HTTPSuccess
      return "#{response.code} OK"
    else
      return "#{response.code} ERROR"
    end
  end
 
  def delete(id)
    request = Net::HTTP::Delete.new("#{@url}/#{id}.json")
    http = Net::HTTP.new(@uri.host, @uri.port)
    response = http.request(request)
 
    # no response body will be returned
    case response
    when Net::HTTPSuccess
      return "#{response.code} OK"
    else
      return "#{response.code} ERROR"
    end
  end
 
end
