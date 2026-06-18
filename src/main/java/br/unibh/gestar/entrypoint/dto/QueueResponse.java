package br.unibh.gestar.entrypoint.dto;

public record QueueResponse(
    int red, 
    int orange, 
    int yellow, 
    int green, 
    int total, 
    NextInQueue next
) {
    public record NextInQueue(String id, String urgencyLevel, String patient) {
    }
}
