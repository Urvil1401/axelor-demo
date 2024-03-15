package com.axelor.account.db.repo;

import com.axelor.account.db.Move;
import com.axelor.account.db.MoveLine;

public class MoveAccountRepository extends MoveRepository {

    @Override
    public Move save(Move move) {

        String moveReference = generateReference("MOVE", move.getId());
        move.setReference(moveReference);

        for (MoveLine moveLine : move.getMoveLineList()) {
            String moveLineReference = generateReference("ML", moveLine.getId());
            moveLine.setReference(moveLineReference);
        }

        move = super.save(move);

        return move;
    }

    private String generateReference(String prefix, Long id) {
        return String.format("%s%03d", prefix, id);
    }
}
