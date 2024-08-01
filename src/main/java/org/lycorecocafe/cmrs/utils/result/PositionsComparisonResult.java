package org.lycorecocafe.cmrs.utils.result;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PositionsComparisonResult {
    private List<BlockPos> added;
    private List<BlockPos> removed;
//    private List<BlockPos> changed_add = new ArrayList<>();
//    private List<BlockPos> changed_remov = new ArrayList<>();
    @Deprecated
    public PositionsComparisonResult(List<BlockPos> added, List<BlockPos> removed) {
        this.added = added;
        this.removed = removed;
    }

    public PositionsComparisonResult() {
        this.added = new ArrayList<>();
        this.removed = new ArrayList<>();
    }

    public List<BlockPos> getAdded() {
        return added;
    }

    public List<BlockPos> getRemoved() {
        return removed;
    }

    public PositionsComparisonResult compare(List<BlockPos> newPositions, List<BlockPos> oldPositions) {
        Set<BlockPos> newSet = new HashSet<>(newPositions);
        Set<BlockPos> oldSet = new HashSet<>(oldPositions);

        Set<BlockPos> added = newSet.stream()
                .filter(pos -> !oldSet.contains(pos))
                .collect(Collectors.toSet());

        Set<BlockPos> removed = oldSet.stream()
                .filter(pos -> !newSet.contains(pos))
                .collect(Collectors.toSet());

        this.added = added.stream().toList();
        this.removed = removed.stream().toList();
        return this;
//        System.out.println("Added: " + added);
//        System.out.println("Removed: " + removed);
    }
}
